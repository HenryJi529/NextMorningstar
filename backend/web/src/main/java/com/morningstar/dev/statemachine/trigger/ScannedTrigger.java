package com.morningstar.dev.statemachine.trigger;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.pojo.po.ActionAttempt;
import com.morningstar.dev.statemachine.*;
import com.morningstar.dev.statemachine.result.ScanResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScannedTrigger implements Trigger {
    private final StateMachineService stateMachineService;
    private final ActionAttemptMapper actionAttemptMapper;

    @Override
    @EventListener
    public void onStateChanged(StateChangedEvent event) {
        if (event.getToState() != State.SCANNED) {
            return;
        }
        if (stateMachineService.isCancelingRun(event.getRunId())) {
            stateMachineService.sendEvent(event.getRunId(), Event.CLEAN);
            return;
        }
        ActionAttempt currentActionAttempt = actionAttemptMapper.selectLatestActionAttempt(event.getRunId(), Action.Type.SCAN);
        ScanResult scanResult = (ScanResult) currentActionAttempt.getResult();
        if (scanResult.getSonarIssueNum() + scanResult.getAiIssueNum() == 0) {
            stateMachineService.sendEvent(event.getRunId(), Event.CLEAN);
        } else {
            stateMachineService.sendEvent(event.getRunId(), Event.FIX);
        }
    }
}
