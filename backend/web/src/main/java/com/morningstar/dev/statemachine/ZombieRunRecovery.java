package com.morningstar.dev.statemachine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Run;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 僵尸任务启动恢复：后端重启时,执行中的 run 定格在 DB(动作随 JVM 死亡，无法推进)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ZombieRunRecovery {

    private static final Map<State, Event> ACTION_DIED_EVENTS = Map.of(
            State.STARTING, Event.START_FAILED,
            State.SYNCING, Event.SYNC_FAILED,
            State.SCANNING, Event.SCAN_FAILED,
            State.FIXING, Event.FIX_FAILED,
            State.VERIFYING, Event.VERIFY_FAILED,
            State.SUBMITTING, Event.SUBMIT_FAILED,
            State.RESTORING, Event.RESTORE_FAILED,
            State.CLEANING, Event.CLEAN_FAILED
    );

    private static final Set<State> TRIGGER_REPLAY_STATES = EnumSet.of(
            State.STARTED, State.SYNCED, State.SCANNED, State.FIXED,
            State.VERIFIED, State.SUBMITTED, State.RESTORED, State.FAILED
    );

    private final RunMapper runMapper;

    private final StateMachineService stateMachineService;

    private final ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    public void recover() {
        LambdaQueryWrapper<Run> wrapper = new LambdaQueryWrapper<>();
        wrapper.notIn(Run::getState, State.PENDING, State.CLEANED);
        List<Run> zombies = runMapper.selectList(wrapper);
        if (zombies.isEmpty()) {
            return;
        }
        log.warn("发现 {} 个上次运行遗留的僵尸任务，开始恢复", zombies.size());
        for (Run zombie : zombies) {
            recoverOne(zombie);
        }
    }

    private void recoverOne(Run zombie) {
        State state = zombie.getState();
        if (zombie.getStatus() == Run.Status.CANCELING) {
            // 重启丢失了内存取消标记，让重试走 FAILED 分支而不是续跑
            stateMachineService.requestCancel(zombie.getId());
        }
        if (ACTION_DIED_EVENTS.containsKey(state)) {
            // "进行中"状态
            Event event = ACTION_DIED_EVENTS.get(state);
            log.warn("[{}] 僵尸任务定格于状态[{}]，补发事件[{}]", zombie.getId(), state, event);
            stateMachineService.sendEvent(zombie.getId(), event);
        } else if (TRIGGER_REPLAY_STATES.contains(state)) {
            // "已完成"状态
            log.warn("[{}] 僵尸任务定格于状态[{}]，重发状态变化通知触发对应Trigger", zombie.getId(), state);
            eventPublisher.publishEvent(new StateChangedEvent(this, zombie.getId(), state, state));
        }
    }
}
