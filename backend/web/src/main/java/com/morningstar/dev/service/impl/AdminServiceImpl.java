package com.morningstar.dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.bo.Stats;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.service.AdminService;
import com.morningstar.dev.service.RunService;
import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.system.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final RunService runService;
    private final ProjectMapper projectMapper;
    private final RunMapper runMapper;
    private final IssueMapper issueMapper;
    private final StateMachineService stateMachineService;

    @Value("${morningstar.app.dev.schedule.max-concurrency}")
    private int maxConcurrency;

    @Override
    public void cancelRun(UUID runId) {
        Run run = runMapper.selectById(runId);
        if (run == null) {
            throw new BaseException(ResponseCode.DEV_RUN_NOT_FOUND, runId);
        }
        Project project = projectMapper.selectById(run.getProjectId());
        if (project == null) {
            throw new BaseException(ResponseCode.DEV_RUN_NOT_FOUND, runId);
        }
        log.info("管理员[{}]取消项目[{}]的当前任务[{}](取消前状态[{}])",
                AuthUtil.getUsername(), project.getName(), runId, run.getState());
        stateMachineService.requestCancel(runId);
    }

    @Override
    public void disableProject(UUID projectId) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BaseException(ResponseCode.DEV_PROJECT_NOT_FOUND, projectId);
        }
        if (Boolean.FALSE.equals(project.getEnabled())) {
            return;
        }
        projectMapper.updateById(Project.builder().id(projectId).enabled(false).build());
        log.info("管理员[{}]停用项目[{}({})]", AuthUtil.getUsername(), project.getName(), projectId);
    }

    @Override
    public Stats getStats() {
        List<UUID> succeededRunIds = runMapper.selectList(
                        new LambdaQueryWrapper<Run>().eq(Run::getStatus, Run.Status.SUCCEEDED))
                .stream().map(Run::getId).toList();
        return Stats.builder()
                .projectCount(Math.toIntExact(projectMapper.selectCount(null)))
                .enabledProjectCount(Math.toIntExact(projectMapper.selectCount(
                        new LambdaQueryWrapper<Project>().eq(Project::getEnabled, true))))
                .executingRunCount(runService.countExecutingRun())
                .pendingRunCount(Math.toIntExact(runMapper.selectCount(
                        new LambdaQueryWrapper<Run>().eq(Run::getState, State.PENDING))))
                .maxConcurrency(maxConcurrency)
                .deliveredIssueCount(succeededRunIds.isEmpty() ? 0 : Math.toIntExact(issueMapper.selectCount(
                        new LambdaQueryWrapper<Issue>()
                                .in(Issue::getRunId, succeededRunIds)
                                .in(Issue::getStatus,
                                        Issue.Status.VERIFIED, Issue.Status.ACCEPTED, Issue.Status.REJECTED))))
                .prTotal(Math.toIntExact(runMapper.selectCount(
                        new LambdaQueryWrapper<Run>().isNotNull(Run::getPrId))))
                .prMerged(Math.toIntExact(runMapper.selectCount(
                        new LambdaQueryWrapper<Run>().eq(Run::getPrStatus, Run.PrStatus.MERGED))))
                .build();
    }
}
