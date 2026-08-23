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
import com.morningstar.dev.statemachine.action.CommonSteps;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.ScanResult;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.dev.util.ProcessUtil;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.PageResult;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.infra.util.CopyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
    private final ProcessUtil processUtil;
    private final CommonSteps commonSteps;

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
                        ? wrapper.orderByAsc(Run::getCreateTime).orderByAsc(Run::getUpdateTime)
                        : wrapper.orderByDesc(Run::getUpdateTime).orderByDesc(Run::getCreateTime));
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
    public void forceClean(UUID runId) {
        Run run = runMapper.selectById(runId);
        log.warn("[{}] 清理卡死任务: 当前状态={}, 上次状态流转时间={}", runId, run.getState(), run.getUpdateTime());
        // 先杀容器再删 volume(容器挂载期间 volume rm 会被拒)，"不存在"视为已清理，其余失败记 error 日志后继续
        try {
            processUtil.run("docker", "container", "rm", "-f", commonSteps.getContainerName(run));
        } catch (ProcessUtil.ProcessExecutionException e) {
            if (!e.getMessage().contains("No such container")) {
                log.error("[{}] 清理卡死任务失败，容器可能残留，需人工排查", run.getId(), e);
            }
        }
        try {
            processUtil.run("docker", "volume", "rm", commonSteps.getVolumeName(run));
        } catch (ProcessUtil.ProcessExecutionException e) {
            if (!e.getMessage().contains("no such volume")) {
                log.error("[{}] 清理卡死任务失败，Volume 可能残留，需人工排查", run.getId(), e);
            }
        }
        // 无论清理成败都落终态
        runMapper.updateById(Run.builder().id(runId).state(State.CLEANED).status(Run.Status.FAILED).build());
        // 手动取消后又卡死的情况，清掉取消标记防泄漏
        stateMachineService.clearCancelingFlag(runId);
        log.warn("[{}] 清理卡死任务完成", runId);
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
            // PR 状态回写不算 run 活动：带上原 updateTime(strict fill 不覆盖非空值)，防止结束时间/执行时长被刷新
            runMapper.updateById(Run.builder().id(runId).prStatus(Run.PrStatus.MERGED).updateTime(run.getUpdateTime()).build());
        } else if (pr.getState() == GiteaUtil.PullRequest.State.CLOSED) {
            issueMapper.update(
                    Issue.builder().status(Issue.Status.REJECTED).build(),
                    new LambdaUpdateWrapper<Issue>()
                            .eq(Issue::getRunId, runId)
                            .eq(Issue::getStatus, Issue.Status.VERIFIED));
            runMapper.updateById(Run.builder().id(runId).prStatus(Run.PrStatus.CLOSED).updateTime(run.getUpdateTime()).build());
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
        List<ActionAttemptBrief> briefs = listAttemptBriefs(run.getId());
        detail.setActionAttemptBriefs(briefs);
        // 等待/执行时长只在终态后计算，执行起点 = START 阶段的开始时刻
        if (run.getState() == State.CLEANED) {
            briefs.stream().filter(b -> b.getActionType() == Action.Type.START).findFirst()
                    .ifPresent(start -> {
                        detail.setWaitSeconds((int) Duration.between(run.getCreateTime(), start.getCreateTime()).getSeconds());
                        detail.setExecSeconds((int) Duration.between(start.getCreateTime(), run.getUpdateTime()).getSeconds());
                    });
        }
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
