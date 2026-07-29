package com.morningstar.dev.statemachine;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CancelTracker {
    private final Set<UUID> cancelingRuns = ConcurrentHashMap.newKeySet();

    public void add(UUID runId) {
        cancelingRuns.add(runId);
    }

    public boolean contains(UUID runId) {
        return cancelingRuns.contains(runId);
    }

    public void remove(UUID runId) {
        cancelingRuns.remove(runId);
    }
}
