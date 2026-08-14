package com.morningstar.dev.service;

import java.util.UUID;

public interface AdminService {
    void cancelRun(UUID runId);

    void disableProject(UUID projectId);
}
