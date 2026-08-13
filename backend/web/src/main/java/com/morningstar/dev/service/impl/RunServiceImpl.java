package com.morningstar.dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.service.RunService;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RunServiceImpl implements RunService {
    private final ProjectMapper projectMapper;
    private final RunMapper runMapper;
    private final StateMachineService stateMachineService;
    private final GiteaUtil giteaUtil;
    private final IssueMapper issueMapper;

    @Override
    public Run createRun(UUID projectId) {
        Run run = Run.builder()
                .id(UUID.randomUUID())
                .projectId(projectId)
                .state(State.PENDING)
                .status(Run.Status.RUNNING)
                .build();
        runMapper.insert(run);
        log.info("[{}] 创建 run 成功，projectId={}", run.getId(), projectId);
        return run;
    }

    @Override
    public Run triggerRun(UUID projectId, UUID adminId) {
        Project dbProject = projectMapper.selectById(projectId);
        if (dbProject == null) {
            throw new BaseException(ResponseCode.DEV_PROJECT_NOT_FOUND, projectId);
        }
        if (!dbProject.getAdminId().equals(adminId)) {
            throw new BaseException(ResponseCode.DEV_PROJECT_ACCESS_DENIED, projectId);
        }

        Run run = createRun(projectId);
        stateMachineService.sendEvent(run.getId(), Event.START);
        return runMapper.selectById(run.getId());
    }

    @Override
    public Run getRun(UUID runId) {
        Run run = runMapper.selectById(runId);
        if (run == null) {
            throw new BaseException(ResponseCode.DEV_RUN_NOT_FOUND, runId);
        }

        return syncPrStatus(run.getId());
    }

    @Override
    public void cancelRun(UUID runId, UUID adminId) {
        Run run = getRun(runId);
        if (!projectMapper.selectById(run.getProjectId()).getAdminId().equals(adminId)) {
            throw new BaseException(ResponseCode.DEV_RUN_ACCESS_DENIED, runId);
        }
        stateMachineService.requestCancel(runId);
    }

    @Override
    public Run syncPrStatus(UUID runId) {
        Run run = runMapper.selectById(runId);
        // 无 PR 或已达终态 → 幂等跳过
        if (run == null || run.getPrId() == null || run.getPrStatus() != Run.PrStatus.OPEN) {
            return run;
        }

        Project project = projectMapper.selectById(run.getProjectId());
        if (project == null) { // project 已删（run 已 CLEANED 但 PR 未处理时可能发生）
            return run;
        }

        GiteaUtil.PullRequest pr = giteaUtil.getPullRequest(project.getLink(), run.getPrId());
        if (pr == null) { // 404：PR 被手动删了，跳过
            return run;
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

        return runMapper.selectById(runId);
    }
}
