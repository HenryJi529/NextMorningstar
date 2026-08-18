package com.morningstar.dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.bo.ActionAttemptBrief;
import com.morningstar.dev.pojo.bo.RunDetail;
import com.morningstar.dev.pojo.bo.SortDir;
import com.morningstar.dev.pojo.po.ActionAttempt;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.service.RunService;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.ScanResult;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.PageResult;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.infra.util.CopyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RunServiceImpl implements RunService {
    private static final Set<Issue.Status> FIXED_AND_BEYOND = EnumSet.of(
            Issue.Status.FIXED, Issue.Status.VERIFIED, Issue.Status.ACCEPTED, Issue.Status.REJECTED);
    private static final Set<Issue.Status> VERIFIED_AND_BEYOND = EnumSet.of(
            Issue.Status.VERIFIED, Issue.Status.ACCEPTED, Issue.Status.REJECTED);

    private final ProjectMapper projectMapper;
    private final RunMapper runMapper;
    private final StateMachineService stateMachineService;
    private final GiteaUtil giteaUtil;
    private final IssueMapper issueMapper;
    private final ActionAttemptMapper actionAttemptMapper;

    @Value("${morningstar.app.dev.schedule.max-concurrency}")
    private int maxConcurrency;

    @Override
    public RunDetail createRun(UUID projectId, Run.TriggerType triggerType) {
        Run run = Run.builder()
                .id(UUID.randomUUID())
                .projectId(projectId)
                .state(State.PENDING)
                .status(Run.Status.RUNNING)
                .triggerType(triggerType)
                .build();
        runMapper.insert(run);
        log.info("[{}] 创建 run 成功，projectId={}，triggerType={}", run.getId(), projectId, triggerType);
        return toDetail(run);
    }

    @Override
    public RunDetail triggerRun(UUID projectId, UUID adminId) {
        Project dbProject = projectMapper.selectById(projectId);
        if (dbProject == null) {
            throw new BaseException(ResponseCode.DEV_PROJECT_NOT_FOUND, projectId);
        }
        if (!dbProject.getAdminId().equals(adminId)) {
            throw new BaseException(ResponseCode.DEV_PROJECT_ACCESS_DENIED, projectId);
        }

        // 已有非终态 run 则拒绝再触发
        if (hasActiveRun(projectId)) {
            throw new BaseException(ResponseCode.DEV_PROJECT_HAS_ACTIVE_RUN, projectId);
        }

        Run run = createRun(projectId, Run.TriggerType.MANUAL);
        if (countExecutingRun() < maxConcurrency) {
            stateMachineService.sendEvent(run.getId(), Event.START);
        } else {
            log.info("[{}] 并发槽已满，run 排队等待 dispatch", run.getId());
        }
        return toDetail(runMapper.selectById(run.getId()));
    }

    @Override
    public RunDetail getRun(UUID runId) {
        Run run = runMapper.selectById(runId);
        if (run == null) {
            throw new BaseException(ResponseCode.DEV_RUN_NOT_FOUND, runId);
        }

        return toDetail(run);
    }

    @Override
    public PageResult<RunDetail> listRun(UUID projectId, List<Run.Status> statuses, int pageNum, int pageSize, SortDir sortDir) {
        LambdaQueryWrapper<Run> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(Run::getProjectId, projectId);
        }
        wrapper.in(statuses != null && !statuses.isEmpty(), Run::getStatus, statuses);
        Page<Run> page = runMapper.selectPage(new Page<>(pageNum, pageSize),
                sortDir == SortDir.ASC
                        ? wrapper.orderByAsc(Run::getCreateTime)
                        : wrapper.orderByDesc(Run::getCreateTime));
        return new PageResult<>(toDetails(page.getRecords()), pageNum, pageSize, page.getTotal());
    }

    @Override
    public void cancelRun(UUID runId, UUID adminId) {
        Run run = runMapper.selectById(runId);
        if (run == null) {
            throw new BaseException(ResponseCode.DEV_RUN_NOT_FOUND, runId);
        }
        Project project = projectMapper.selectById(run.getProjectId());
        if (project == null) {
            throw new BaseException(ResponseCode.DEV_RUN_NOT_FOUND, runId);
        }
        if (!project.getAdminId().equals(adminId)) {
            throw new BaseException(ResponseCode.DEV_RUN_ACCESS_DENIED, runId);
        }
        stateMachineService.requestCancel(runId);
    }

    @Override
    public void syncPrStatus(UUID runId) {
        Run run = runMapper.selectById(runId);
        // 无 PR 或已达终态 → 幂等跳过
        if (run == null || run.getPrId() == null || run.getPrStatus() != Run.PrStatus.OPEN) {
            return;
        }

        Project project = projectMapper.selectById(run.getProjectId());
        if (project == null) { // project 已删（run 已 CLEANED 但 PR 未处理时可能发生）
            return;
        }

        GiteaUtil.PullRequest pr = giteaUtil.getPullRequest(project.getLink(), run.getPrId());
        if (pr == null) { // 404：PR 被手动删了，跳过
            return;
        }

        if (Boolean.TRUE.equals(pr.getMerged())) {
            issueMapper.update(
                    Issue.builder().status(Issue.Status.ACCEPTED).build(),
                    new LambdaUpdateWrapper<Issue>()
                            .eq(Issue::getRunId, runId)
                            .eq(Issue::getStatus, Issue.Status.VERIFIED));
            runMapper.updateById(Run.builder().id(runId).prStatus(Run.PrStatus.MERGED).build());
        } else if (pr.getState() == GiteaUtil.PullRequest.State.CLOSED) {
            issueMapper.update(
                    Issue.builder().status(Issue.Status.REJECTED).build(),
                    new LambdaUpdateWrapper<Issue>()
                            .eq(Issue::getRunId, runId)
                            .eq(Issue::getStatus, Issue.Status.VERIFIED));
            runMapper.updateById(Run.builder().id(runId).prStatus(Run.PrStatus.CLOSED).build());
        }
        // open → 不变
    }

    @Override
    public boolean hasActiveRun(UUID projectId) {
        return runMapper.selectCount(
                new LambdaQueryWrapper<Run>()
                        .eq(Run::getProjectId, projectId)
                        .ne(Run::getState, State.CLEANED)) > 0;
    }

    @Override
    public int countExecutingRun() {
        // 查当前有多少 run 正在执行中（非 PENDING 且非终态）
        return Math.toIntExact(runMapper.selectCount(
                new LambdaQueryWrapper<Run>()
                        .notIn(Run::getState, State.PENDING, State.CLEANED)
        ));
    }

    private RunDetail toDetail(Run run) {
        RunDetail detail = new RunDetail();
        CopyUtil.copyNonNullProperties(run, detail);
        Project project = projectMapper.selectById(run.getProjectId());
        if (project != null) { // project 可能已删
            detail.setProjectName(project.getName());
            if (run.getPrId() != null) {
                detail.setPrLink(giteaUtil.getPrLink(project.getLink(), run.getPrId()));
            }
        }
        ActionAttempt latestScanAttempt = actionAttemptMapper.selectLatestActionAttempt(run.getId(), Action.Type.SCAN);
        if (latestScanAttempt != null && latestScanAttempt.getResult() instanceof ScanResult scanResult
                && scanResult.getStatus() == ActionResult.Status.SUCCEEDED) {
            detail.setScannedIssueCount(scanResult.getScannedSonarIssueNum() + scanResult.getScannedAiIssueNum());
            List<Issue.Status> issueStatuses = issueMapper.selectList(
                            new LambdaQueryWrapper<Issue>()
                                    .select(Issue::getStatus)
                                    .eq(Issue::getRunId, run.getId()))
                    .stream().map(Issue::getStatus).toList();
            detail.setSelectedIssueCount(issueStatuses.size());
            /* 已修复Issue状态：FIXED 及之后 */
            detail.setCurrentFixedIssueCount(Math.toIntExact(issueStatuses.stream()
                    .filter(FIXED_AND_BEYOND::contains).count()));
            /* 已验证Issue状态：VERIFIED 及之后 */
            detail.setCurrentVerifiedIssueCount(Math.toIntExact(issueStatuses.stream()
                    .filter(VERIFIED_AND_BEYOND::contains).count()));
            if (run.getStatus() == Run.Status.SUCCEEDED) {
                detail.setDeliveredIssueCount(detail.getCurrentVerifiedIssueCount());
            }
        }
        detail.setActionAttemptBriefs(listAttemptBriefs(run.getId()));
        return detail;
    }

    private List<RunDetail> toDetails(List<Run> runs) {
        return runs.stream().map(this::toDetail).toList();
    }

    private List<ActionAttemptBrief> listAttemptBriefs(UUID runId) {
        return actionAttemptMapper.selectList(
                        new LambdaQueryWrapper<ActionAttempt>()
                                .select(ActionAttempt::getActionType, ActionAttempt::getAttemptNo,
                                        ActionAttempt::getStatus, ActionAttempt::getCreateTime, ActionAttempt::getUpdateTime)
                                .eq(ActionAttempt::getRunId, runId)
                                .orderByAsc(ActionAttempt::getId))
                .stream().map(attempt -> {
                    ActionAttemptBrief brief = new ActionAttemptBrief();
                    CopyUtil.copyNonNullProperties(attempt, brief);
                    return brief;
                }).toList();
    }
}
