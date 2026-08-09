package com.morningstar.dev.statemachine.trigger;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.ActionAttempt;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.statemachine.*;
import com.morningstar.dev.statemachine.result.ScanResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanedTrigger implements Trigger {
    private final StateMachineService stateMachineService;
    private final RunMapper runMapper;
    private final ActionAttemptMapper actionAttemptMapper;

    @Override
    @EventListener
    public void onStateChanged(StateChangedEvent event) {
        if (event.getToState() != State.CLEANED) {
            return;
        }

        if (stateMachineService.isCancelingRun(event.getRunId())) {
            runMapper.updateById(Run.builder().id(event.getRunId()).status(Run.Status.CANCELED).build());
            stateMachineService.clearCancelingFlag(event.getRunId());
            return;
        }

        ActionAttempt latestScanAttempt = actionAttemptMapper.selectLatestActionAttempt(event.getRunId(), Action.Type.SCAN);
        if (latestScanAttempt != null && latestScanAttempt.getStatus() == ActionStatus.SUCCEEDED) {
            ScanResult latestScanResult = (ScanResult) latestScanAttempt.getResult();
            if (latestScanResult.getSonarIssueNum() + latestScanResult.getAiIssueNum() == 0) {
                // NOTE: 扫描完成且没发现问题，直接成功
                runMapper.updateById(Run.builder().id(event.getRunId()).status(Run.Status.SUCCEEDED).build());
                return;
            }
        }

        ActionAttempt latestSubmitAttempt = actionAttemptMapper.selectLatestActionAttempt(event.getRunId(), Action.Type.SUBMIT);
        if (latestSubmitAttempt != null && latestSubmitAttempt.getStatus() == ActionStatus.SUCCEEDED) {
            runMapper.updateById(Run.builder().id(event.getRunId()).status(Run.Status.SUCCEEDED).build());
            return;
        }

        runMapper.updateById(Run.builder().id(event.getRunId()).status(Run.Status.FAILED).build());

    }
}
