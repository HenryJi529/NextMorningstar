package com.morningstar.dev.statemachine.trigger;

import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateChangedEvent;
import com.morningstar.dev.statemachine.Trigger;
import com.morningstar.dev.statemachine.action.SubmitAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubmittingTrigger implements Trigger {
    private final SubmitAction submitAction;

    @Override
    @EventListener
    public void onStateChanged(StateChangedEvent event) {
        if (event.getToState() != State.SUBMITTING) {
            return;
        }
        submitAction.execute(event.getRunId());
    }
}
