package com.morningstar.dev.web.controller;

import com.morningstar.dev.pojo.bo.RunDetail;
import com.morningstar.dev.pojo.bo.SortDir;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.service.RunService;
import com.morningstar.infra.response.PageResult;
import com.morningstar.infra.response.R;
import com.morningstar.system.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "工坊任务相关接口定义")
@RestController
@RequestMapping("/dev/run")
@RequiredArgsConstructor
public class RunController {
    private final RunService runService;

    @Operation(summary = "手动触发任务")
    @PostMapping
    public R<RunDetail> trigger(@RequestParam UUID projectId) {
        return R.ok(runService.triggerRun(projectId, AuthUtil.getUserId()));
    }

    @Operation(summary = "获取任务列表")
    @GetMapping("")
    public R<PageResult<RunDetail>> list(@RequestParam(required = false) UUID projectId,
                                         @RequestParam(required = false) List<Run.Status> statuses,
                                         @RequestParam("pageNum") @Positive(message = "pageNum必须大于0") int pageNum,
                                         @RequestParam("pageSize") @Positive(message = "pageSize必须大于0") int pageSize,
                                         @RequestParam("sortDir") SortDir sortDir) {
        return R.ok(runService.listRun(projectId, statuses, pageNum, pageSize, sortDir));
    }

    @Operation(summary = "获取任务")
    @GetMapping("/{id}")
    public R<RunDetail> getById(@PathVariable UUID id) {
        return R.ok(runService.getRun(id));
    }

    @Operation(summary = "取消任务")
    @DeleteMapping("/{id}")
    public R<Object> cancel(@PathVariable UUID id) {
        runService.cancelRun(id, AuthUtil.getUserId());
        return R.ok();
    }
}
