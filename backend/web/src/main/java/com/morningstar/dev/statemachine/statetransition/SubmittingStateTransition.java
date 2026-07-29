package com.morningstar.dev.statemachine.statetransition;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.properties.MaxAttemptsProperties;
import com.morningstar.dev.statemachine.*;
import com.morningstar.dev.statemachine.exception.StateTransitionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubmittingStateTransition implements StateTransition {
    private final ActionAttemptMapper actionAttemptMapper;
    private final MaxAttemptsProperties maxAttemptsProperties;
    private final CancelTracker cancelTracker;

    @Override
    public State getCurrentState() {
        return State.SUBMITTING;
    }

    @Override
    public State getNextState(UUID runId, Event event) {
        if (event == Event.SUBMIT_SUCCEEDED) {
            return State.SUBMITTED;
        }
        if (event == Event.SUBMIT_FAILED) {
            if (actionAttemptMapper.selectMaxAttemptNo(runId, Action.Type.SUBMIT) < maxAttemptsProperties.getSubmit()
                    && !cancelTracker.contains(runId)) {
                return State.SUBMITTING;
            } else {
                return State.FAILED;
            }
        }
        throw new StateTransitionDeniedException(getCurrentState(), event);
    }
}
