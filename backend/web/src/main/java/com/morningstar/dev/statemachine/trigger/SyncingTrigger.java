package com.morningstar.dev.statemachine.trigger;

import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateChangedEvent;
import com.morningstar.dev.statemachine.Trigger;
import com.morningstar.dev.statemachine.action.SyncAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SyncingTrigger implements Trigger {

    private final SyncAction syncAction;

    @Override
    @EventListener
    public void onStateChanged(StateChangedEvent event) {
        if (event.getToState() != State.SYNCING) {
            return;
        }
        syncAction.execute(event.getRunId());
    }
}
