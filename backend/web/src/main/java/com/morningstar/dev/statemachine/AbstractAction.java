package com.morningstar.dev.statemachine;


import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.pojo.po.ActionAttempt;
import com.morningstar.dev.statemachine.result.ActionResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.UUID;

@Slf4j
@Getter
public abstract class AbstractAction implements Action {
    private final StateMachineService stateMachineService;
    private final ActionAttemptMapper actionAttemptMapper;
    private final Event successEvent;
    private final Event failureEvent;

    protected AbstractAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, Event successEvent, Event failureEvent) {
        this.stateMachineService = stateMachineService;
        this.actionAttemptMapper = actionAttemptMapper;
        this.successEvent = successEvent;
        this.failureEvent = failureEvent;
    }

    @Override
    @Async
    public void execute(UUID runId) {
        int currentAttemptNo = actionAttemptMapper.selectMaxAttemptNo(runId, getType()) + 1;
        ActionAttempt currentActionAttempt = ActionAttempt
                .builder()
                .runId(runId)
                .actionType(getType())
                .attemptNo(currentAttemptNo)
                .status(ActionStatus.RUNNING)
                .build();
        actionAttemptMapper.insert(currentActionAttempt);
        log.info("[{}] {} 正在执行...", runId, getType());
        ActionResult actionResult = doExecute(runId);
        currentActionAttempt.setResult(actionResult);
        if (actionResult.getStatus() == ActionResult.Status.SUCCEEDED) {
            currentActionAttempt.setStatus(ActionStatus.SUCCEEDED);
            actionAttemptMapper.updateById(currentActionAttempt);
            log.info("[{}] {} 执行成功 ☑️", runId, getType());
            stateMachineService.sendEvent(runId, successEvent);
        } else {
            currentActionAttempt.setStatus(ActionStatus.FAILED);
            actionAttemptMapper.updateById(currentActionAttempt);
            log.info("[{}] {} 执行失败 ❌", runId, getType());
            stateMachineService.sendEvent(runId, failureEvent);
        }
    }

    protected abstract ActionResult doExecute(UUID runId);
}
