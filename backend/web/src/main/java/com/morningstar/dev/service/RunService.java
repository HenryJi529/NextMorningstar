package com.morningstar.dev.service;

import com.morningstar.dev.pojo.bo.RunDetail;

import java.util.List;
import java.util.UUID;

public interface RunService {
    RunDetail createRun(UUID projectId);

    RunDetail triggerRun(UUID projectId, UUID adminId);

    RunDetail getRun(UUID runId);

    List<RunDetail> listRun(UUID projectId, UUID adminId);

    void cancelRun(UUID runId, UUID adminId);

    void syncPrStatus(UUID runId);

    boolean hasActiveRun(UUID projectId);

    int countExecutingRun();
}
