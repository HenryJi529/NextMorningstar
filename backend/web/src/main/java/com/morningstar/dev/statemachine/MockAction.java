package com.morningstar.dev.statemachine;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.statemachine.result.ActionResult;

import java.util.UUID;

public class MockAction extends AbstractAction {
    private final double successRatio;

    public MockAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, double successRatio, Event successEvent, Event failureEvent) {
        super(stateMachineService, actionAttemptMapper, successEvent, failureEvent);
        this.successRatio = successRatio;
    }

    public MockAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, Event successEvent, Event failureEvent) {
        this(stateMachineService, actionAttemptMapper, 1.0, successEvent, failureEvent);
    }

    @Override
    public Action.Type getType() {
        return Action.Type.MOCK;
    }

    @Override
    protected ActionResult doExecute(UUID runId) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        return ActionResult
                .builder()
                .status(Math.random() < successRatio ? ActionResult.Status.SUCCEEDED : ActionResult.Status.FAILED)
                .message("This is Mock Result")
                .build();
    }
}
