package com.morningstar.dev.statemachine.action;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.CleanResult;
import com.morningstar.dev.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class CleanAction extends AbstractAction {
    private final ProcessUtil processUtil;
    private final CommonSteps commonSteps;
    private final RunMapper runMapper;

    public CleanAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, ProcessUtil processUtil, CommonSteps commonSteps, RunMapper runMapper) {
        super(stateMachineService, actionAttemptMapper, Event.CLEAN_SUCCEEDED, Event.CLEAN_FAILED);
        this.processUtil = processUtil;
        this.commonSteps = commonSteps;
        this.runMapper = runMapper;
    }

    @Override
    public Action.Type getType() {
        return Action.Type.CLEAN;
    }

    @Override
    protected CleanResult doExecute(UUID runId) {
        Run run = runMapper.selectById(runId);
        String containerName = commonSteps.getContainerName(run);

        // 删除 Docker 容器
        try {
            processUtil.run("docker", "container", "rm", "-f", containerName);
        } catch (ProcessUtil.ProcessExecutionException e) {
            if (!e.getMessage().contains("No such container")) {
                return CleanResult.builder().status(ActionResult.Status.FAILED).message(e.getMessage()).build();
            }
        }
        return CleanResult
                .builder()
                .status(ActionResult.Status.SUCCEEDED)
                .containerName(containerName)
                .build();
    }
}