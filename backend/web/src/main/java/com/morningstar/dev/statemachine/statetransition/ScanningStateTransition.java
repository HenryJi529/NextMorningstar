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
public class ScanningStateTransition implements StateTransition {
    private final ActionAttemptMapper actionAttemptMapper;
    private final MaxAttemptsProperties maxAttemptsProperties;
    private final CancelTracker cancelTracker;

    @Override
    public State getCurrentState() {
        return State.SCANNING;
    }

    @Override
    public State getNextState(UUID runId, Event event) {
        if (event == Event.SCAN_SUCCEEDED) {
            return State.SCANNED;
        }
        if (event == Event.SCAN_FAILED) {
            if (actionAttemptMapper.selectMaxAttemptNo(runId, Action.Type.SCAN) < maxAttemptsProperties.getScan()
                    && !cancelTracker.contains(runId)) {
                return State.SCANNING;
            } else {
                return State.FAILED;
            }
        }

        throw new StateTransitionDeniedException(getCurrentState(), event);
    }
}
