package com.morningstar.dev.statemachine.trigger;

import com.morningstar.dev.statemachine.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartedTrigger implements Trigger {
    private final StateMachineService stateMachineService;

    @Override
    @EventListener
    public void onStateChanged(StateChangedEvent event) {
        if (event.getToState() != State.STARTED) {
            return;
        }
        stateMachineService.sendEvent(event.getRunId(), Event.SYNC);
    }
}
