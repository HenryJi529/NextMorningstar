package com.morningstar.dev.service;

import com.morningstar.dev.pojo.po.Run;

import java.util.UUID;

public interface RunService {
    Run createRun(UUID projectId);

    Run triggerRun(UUID projectId, UUID adminId);

    Run getRun(UUID runId, UUID adminId);

    void cancelRun(UUID runId, UUID adminId);
}
