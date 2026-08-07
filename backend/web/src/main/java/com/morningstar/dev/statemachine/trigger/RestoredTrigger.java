package com.morningstar.dev.statemachine.trigger;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.pojo.po.ActionAttempt;
import com.morningstar.dev.properties.MaxAttemptsProperties;
import com.morningstar.dev.statemachine.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestoredTrigger implements Trigger {
    private final StateMachineService stateMachineService;
    private final ActionAttemptMapper actionAttemptMapper;
    private final MaxAttemptsProperties maxAttemptsProperties;
    private final CancelTracker cancelTracker;

    @Override
    @EventListener
    public void onStateChanged(StateChangedEvent event) {
        if (event.getToState() != State.RESTORED) {
            return;
        }

        int fixAttempts = actionAttemptMapper.selectMaxAttemptNo(event.getRunId(), Action.Type.FIX);
        int verifyAttempts = actionAttemptMapper.selectMaxAttemptNo(event.getRunId(), Action.Type.VERIFY);
        ActionAttempt latestFix = actionAttemptMapper.selectLatestActionAttempt(event.getRunId(), Action.Type.FIX);
        ActionAttempt latestVerify = actionAttemptMapper.selectLatestActionAttempt(event.getRunId(), Action.Type.VERIFY);

        if (latestVerify == null || latestFix.getCreateTime().isAfter(latestVerify.getCreateTime())) {
            // 最新一次失败是修复
            if (fixAttempts < maxAttemptsProperties.getFix() && !cancelTracker.contains(event.getRunId())) {
                stateMachineService.sendEvent(event.getRunId(), Event.FIX);
            } else {
                stateMachineService.sendEvent(event.getRunId(), Event.FIX_FAILED);
            }
        } else {
            // 最新一次失败是验证
            if (verifyAttempts < maxAttemptsProperties.getVerify() && !cancelTracker.contains(event.getRunId())) {
                stateMachineService.sendEvent(event.getRunId(), Event.FIX);
            } else {
                stateMachineService.sendEvent(event.getRunId(), Event.VERIFY_FAILED);
            }
        }
    }
}
