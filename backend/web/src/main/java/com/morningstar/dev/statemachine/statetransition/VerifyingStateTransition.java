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
public class VerifyingStateTransition implements StateTransition {
    private final ActionAttemptMapper actionAttemptMapper;
    private final MaxAttemptsProperties maxAttemptsProperties;
    private final CancelTracker cancelTracker;

    @Override
    public State getCurrentState() {
        return State.VERIFYING;
    }

    @Override
    public State getNextState(UUID runId, Event event) {
        if (event == Event.VERIFY_SUCCEEDED) {
            return State.VERIFIED;
        }
        if (event == Event.VERIFY_FAILED) {
            if (actionAttemptMapper.selectMaxAttemptNo(runId, Action.Type.VERIFY) < maxAttemptsProperties.getVerify()
                    && !cancelTracker.contains(runId)) {
                return State.RESTORING;
            } else {
                return State.FAILED;
            }
        }
        throw new StateTransitionDeniedException(getCurrentState(), event);
    }
}
