package com.morningstar.dev.service;

import com.morningstar.dev.pojo.po.Run;

import java.util.List;
import java.util.UUID;

public interface RunService {
    Run createRun(UUID projectId);

    Run triggerRun(UUID projectId, UUID adminId);

    Run getRun(UUID runId);

    List<Run> listRun(UUID projectId, UUID adminId);

    void cancelRun(UUID runId, UUID adminId);

    void syncPrStatus(UUID runId);

    boolean hasActiveRun(UUID projectId);
}
