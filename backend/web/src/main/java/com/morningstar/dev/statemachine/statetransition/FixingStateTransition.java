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
public class FixingStateTransition implements StateTransition {
    @Override
    public State getCurrentState() {
        return State.FIXING;
    }

    @Override
    public State getNextState(UUID runId, Event event) {
        if (event == Event.FIX_SUCCEEDED) {
            return State.FIXED;
        }
        if (event == Event.FIX_FAILED) {
            return State.RESTORING;
        }

        throw new StateTransitionDeniedException(getCurrentState(), event);
    }
}
