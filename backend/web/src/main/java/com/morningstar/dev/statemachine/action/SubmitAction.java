package com.morningstar.dev.statemachine.action;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.MockAction;
import com.morningstar.dev.statemachine.StateMachineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SubmitAction extends MockAction {
    public SubmitAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper) {
        super(stateMachineService, actionAttemptMapper, 0.4, Event.SUBMIT_SUCCEEDED, Event.SUBMIT_FAILED);
    }

    @Override
    public Action.Type getType() {
        return Action.Type.SUBMIT;
    }
}
