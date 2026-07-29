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
public class RestoreAction extends MockAction {
    public RestoreAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper) {
        super(stateMachineService, actionAttemptMapper, Event.RESTORE_SUCCEEDED, Event.RESTORE_FAILED);
    }

    @Override
    public Action.Type getType() {
        return Type.RESTORE;
    }
}
