package com.morningstar.dev.statemachine.statetransition;

import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateTransition;
import com.morningstar.dev.statemachine.exception.StateTransitionDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CleaningStateTransition implements StateTransition {
    @Override
    public State getCurrentState() {
        return State.CLEANING;
    }

    @Override
    public State getNextState(UUID runId, Event event) {
        if (event == Event.CLEAN_SUCCEEDED) {
            return State.CLEANED;
        }
        if (event == Event.CLEAN_FAILED) {
            return State.FAILED;
        }
        throw new StateTransitionDeniedException(getCurrentState(), event);
    }
}
