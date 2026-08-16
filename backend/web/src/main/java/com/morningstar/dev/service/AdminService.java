package com.morningstar.dev.service;

import com.morningstar.dev.pojo.bo.Stats;

import java.util.UUID;

public interface AdminService {
    void cancelRun(UUID runId);

    void toggleSchedule(UUID projectId);

    Stats getStats();
}
