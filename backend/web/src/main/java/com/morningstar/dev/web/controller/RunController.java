package com.morningstar.dev.web.controller;

import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.service.RunService;
import com.morningstar.infra.response.R;
import com.morningstar.system.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "研发任务相关接口定义")
@RestController
@RequestMapping("/dev/run")
@RequiredArgsConstructor
public class RunController {
    private final RunService runService;

    @Operation(summary = "手动触发任务")
    @PostMapping
    public R<Run> trigger(@RequestParam UUID projectId) {
        return R.ok(runService.triggerRun(projectId, AuthUtil.getUserId()));
    }

    @Operation(summary = "获取任务")
    @GetMapping("/{id}")
    public R<Run> getById(@PathVariable UUID id) {
        return R.ok(runService.getRun(id, AuthUtil.getUserId()));
    }

    @Operation(summary = "取消任务")
    @DeleteMapping("/{id}")
    public R<Object> cancel(@PathVariable UUID id) {
        runService.cancelRun(id, AuthUtil.getUserId());
        return R.ok();
    }
}
