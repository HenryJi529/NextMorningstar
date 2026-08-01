package com.morningstar.dev.service.impl;

import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.service.RunService;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateMachineService;
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
    public Run getRun(UUID runId, UUID adminId) {
        Run run = runMapper.selectById(runId);
        if (run == null) {
            throw new BaseException(ResponseCode.DEV_RUN_NOT_FOUND, runId);
        }
        if (!projectMapper.selectById(run.getProjectId()).getAdminId().equals(adminId)) {
            throw new BaseException(ResponseCode.DEV_RUN_ACCESS_DENIED, runId);
        }
        return run;
    }

    @Override
    public void cancelRun(UUID runId, UUID adminId) {
        getRun(runId, adminId);

        stateMachineService.requestCancel(runId);
    }
}
