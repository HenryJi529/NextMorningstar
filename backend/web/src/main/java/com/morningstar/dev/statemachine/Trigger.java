package com.morningstar.dev.statemachine;

public interface Trigger {
    void onStateChanged(StateChangedEvent event);
}
