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
public class VerifyAction extends MockAction {
    public VerifyAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper) {
        super(stateMachineService, actionAttemptMapper, 0.6, Event.VERIFY_SUCCEEDED, Event.VERIFY_FAILED);
    }

    @Override
    public Action.Type getType() {
        return Action.Type.VERIFY;
    }
}
