package com.morningstar.dev.service.impl;

import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.service.AdminService;
import com.morningstar.dev.service.RunService;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.system.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final RunService runService;
    private final ProjectMapper projectMapper;
    private final StateMachineService stateMachineService;

    @Override
    public void cancelRun(UUID runId) {
        Run run = runService.getRun(runId);
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
}
