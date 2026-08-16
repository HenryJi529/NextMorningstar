package com.morningstar.dev.web.controller;

import com.morningstar.dev.pojo.bo.Stats;
import com.morningstar.dev.service.AdminService;
import com.morningstar.infra.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "工坊管理员相关接口定义")
@RestController
@RequestMapping("/dev/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "平台统计")
    @GetMapping("/stats")
    public R<Stats> stats() {
        return R.ok(adminService.getStats());
    }

    @Operation(summary = "取消任务")
    @DeleteMapping("/run/{id}")
    @PreAuthorize("hasAuthority('dev:run:cancel')")
    public R<Object> cancelRun(@PathVariable UUID id) {
        adminService.cancelRun(id);
        return R.ok();
    }

    @Operation(summary = "切换调度启停")
    @PostMapping("/project/{id}/schedule")
    @PreAuthorize("hasAuthority('dev:project:schedule')")
    public R<Object> toggleSchedule(@PathVariable UUID id) {
        adminService.toggleSchedule(id);
        return R.ok();
    }
}
