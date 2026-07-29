package com.morningstar.dev.statemachine.statetransition;

import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateTransition;
import com.morningstar.dev.statemachine.exception.StateTransitionDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RestoringStateTransition implements StateTransition {
    @Override
    public State getCurrentState() {
        return State.RESTORING;
    }

    @Override
    public State getNextState(UUID runId, Event event) {
        if (event == Event.RESTORE_SUCCEEDED) {
            return State.RESTORED;
        }
        if (event == Event.RESTORE_FAILED) {
            return State.FAILED;
        }
        throw new StateTransitionDeniedException(getCurrentState(), event);
    }
}
