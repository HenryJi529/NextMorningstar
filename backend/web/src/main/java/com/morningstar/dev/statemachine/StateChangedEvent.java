package com.morningstar.dev.statemachine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
public class StateChangedEvent extends ApplicationEvent {
    private final UUID runId;
    private final State fromState;
    private final State toState;

    public StateChangedEvent(Object source, UUID runId, State fromState, State toState) {
        super(source);
        this.runId = runId;
        this.fromState = fromState;
        this.toState = toState;
    }
}
