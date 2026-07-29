package com.morningstar.dev.statemachine.trigger;

import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateChangedEvent;
import com.morningstar.dev.statemachine.Trigger;
import com.morningstar.dev.statemachine.action.ScanAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScanningTrigger implements Trigger {
    private final ScanAction scanAction;

    @Override
    @EventListener
    public void onStateChanged(StateChangedEvent event) {
        if (event.getToState() != State.SCANNING) {
            return;
        }
        scanAction.execute(event.getRunId());
    }
}
