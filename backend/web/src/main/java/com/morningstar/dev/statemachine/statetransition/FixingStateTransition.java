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
public class FixingStateTransition implements StateTransition {
    private final ActionAttemptMapper actionAttemptMapper;
    private final MaxAttemptsProperties maxAttemptsProperties;
    private final CancelTracker cancelTracker;

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
            if (actionAttemptMapper.selectMaxAttemptNo(runId, Action.Type.FIX) < maxAttemptsProperties.getFix()
                    && !cancelTracker.contains(runId)) {
                return State.RESTORING;
            } else {
                return State.FAILED;
            }
        }

        throw new StateTransitionDeniedException(getCurrentState(), event);
    }
}
