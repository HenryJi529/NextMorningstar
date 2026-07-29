package com.morningstar.dev.statemachine.exception;

import com.morningstar.dev.statemachine.State;

public class StateTransitionNotFoundException extends RuntimeException {
    public StateTransitionNotFoundException(State state) {
        super("找不到对应的状态处理器: " + state);
    }
}
