package com.morningstar.dev.service;

import com.morningstar.dev.pojo.bo.RunDetail;
import com.morningstar.dev.pojo.bo.SortDir;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.infra.response.PageResult;

import java.util.List;
import java.util.UUID;

public interface RunService {
    RunDetail createRun(UUID projectId, Run.TriggerType triggerType);

    RunDetail triggerRun(UUID projectId, UUID adminId);

    RunDetail getRun(UUID runId);

    PageResult<RunDetail> listRun(UUID projectId, List<Run.Status> statuses, int pageNum, int pageSize, SortDir sortDir);

    void cancelRun(UUID runId, UUID adminId);

    void forceClean(UUID runId);

    void syncPrStatus(UUID runId);

    boolean hasActiveRun(UUID projectId);

    int countExecutingRun();
}
