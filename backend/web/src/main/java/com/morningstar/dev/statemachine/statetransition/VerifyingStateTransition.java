package com.morningstar.dev.statemachine.statetransition;

import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateTransition;
import com.morningstar.dev.statemachine.exception.StateTransitionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VerifyingStateTransition implements StateTransition {
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
            return State.RESTORING;
        }
        throw new StateTransitionDeniedException(getCurrentState(), event);
    }
}
