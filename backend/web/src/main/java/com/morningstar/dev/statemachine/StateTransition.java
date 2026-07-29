package com.morningstar.dev.statemachine;

import java.util.UUID;

public interface StateTransition {
    State getCurrentState();

    State getNextState(UUID runId, Event event);
}