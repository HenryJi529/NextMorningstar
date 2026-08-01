package com.morningstar.dev.statemachine;

import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.statemachine.exception.StateTransitionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class StateMachineService {
    private final Map<State, StateTransition> handlerMap = new EnumMap<>(State.class);
    private final ApplicationEventPublisher eventPublisher;
    private final RunMapper runMapper;
    private final CancelTracker cancelTracker;

    public StateMachineService(List<StateTransition> handlers, ApplicationEventPublisher eventPublisher, RunMapper runMapper, CancelTracker cancelTracker) {
        this.eventPublisher = eventPublisher;

        for (StateTransition handler : handlers) {
            handlerMap.put(handler.getCurrentState(), handler);
        }
        this.runMapper = runMapper;
        this.cancelTracker = cancelTracker;
    }

    /**
     * 触发状态流转
     */
    public synchronized State sendEvent(UUID runId, Event event) {
        Run currentRun = runMapper.selectById(runId);
        State currentState = currentRun.getState();

        StateTransition handler = handlerMap.get(currentState);
        if (handler == null) {
            throw new StateTransitionNotFoundException(currentState);
        }

        // 执行流转并获取新状态
        State nextState = handler.getNextState(runId, event);

        // 必须先落表，再发送事件
        runMapper.updateById(Run.builder().id(currentRun.getId()).state(nextState).build());
        StateChangedEvent changeEvent = new StateChangedEvent(this, runId, currentState, nextState);
        eventPublisher.publishEvent(changeEvent);

        return nextState;
    }

    public void requestCancel(UUID runId) {
        State currentState = runMapper.selectById(runId).getState();
        if (Set.of(State.PENDING, State.SUBMITTED, State.CLEANING, State.CLEANED).contains(currentState)) {
            log.warn("[{}] 当前状态 {} 不允许取消，忽略请求", runId, currentState);
            return;
        }

        cancelTracker.add(runId);
        runMapper.updateById(Run.builder().id(runId).status(Run.Status.CANCELING).build());
        log.info("[{}] 取消任务 ⏹️", runId);
    }

    public boolean isCancelingRun(UUID runId) {
        return cancelTracker.contains(runId);
    }

    public void clearCancelingFlag(UUID runId) {
        cancelTracker.remove(runId);
    }
}
