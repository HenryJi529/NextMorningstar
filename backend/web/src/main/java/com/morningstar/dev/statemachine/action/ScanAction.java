package com.morningstar.dev.statemachine.action;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.MockAction;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.ScanResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ScanAction extends MockAction {
    public ScanAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper) {
        super(stateMachineService, actionAttemptMapper, 1.0, Event.SCAN_SUCCEEDED, Event.SCAN_FAILED);
    }

    @Override
    public Action.Type getType() {
        return Action.Type.SCAN;
    }

    @Override
    protected ScanResult doExecute(UUID runId) {
        ActionResult mockResult = super.doExecute(runId);
        ScanResult scanResult = ScanResult
                .builder()
                .status(mockResult.getStatus())
                .message(mockResult.getMessage())
                .build();
        if (scanResult.getStatus() == ActionResult.Status.SUCCEEDED) {
            scanResult.setIssueNum(Math.random() < 0.5 ? 10 : 0);
        }
        return scanResult;
    }
}
