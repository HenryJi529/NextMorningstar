package com.morningstar.dev.statemachine;

import java.util.UUID;

public interface Step {
    void execute(UUID runId, State currentState);
}
