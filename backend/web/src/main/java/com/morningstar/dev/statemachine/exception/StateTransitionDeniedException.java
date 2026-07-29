package com.morningstar.dev.statemachine.exception;

import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.State;

public class StateTransitionDeniedException extends RuntimeException {
    public StateTransitionDeniedException(State state, Event event) {
        super("当前状态「" + state.getName() + "」不支持事件「" + event.getName() + "」");
    }
}
