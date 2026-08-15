package com.morningstar.dev.web.controller;

import com.morningstar.dev.pojo.bo.ProjectDetail;
import com.morningstar.dev.pojo.vo.CreateProjectRequestVo;
import com.morningstar.dev.pojo.vo.UpdateProjectRequestVo;
import com.morningstar.dev.service.ProjectService;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.R;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.system.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "工坊项目相关接口定义")
@RestController
@RequestMapping("/dev/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "创建项目")
    @PostMapping
    public R<ProjectDetail> create(@Valid @RequestBody CreateProjectRequestVo vo) {
        vo.setAdminId(AuthUtil.getUserId());
        return R.ok(projectService.createProject(vo));
    }

    @Operation(summary = "获取项目(根据id)")
    @GetMapping("/{id}")
    public R<ProjectDetail> getById(@PathVariable UUID id) {
        return R.ok(projectService.getProjectById(id));
    }

    @Operation(summary = "更新项目")
    @PatchMapping("/{id}")
    public R<ProjectDetail> update(@PathVariable UUID id, @Valid @RequestBody UpdateProjectRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        vo.setAdminId(AuthUtil.getUserId());
        return R.ok(projectService.updateProject(vo));
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public R<Object> delete(@PathVariable UUID id) {
        projectService.deleteProject(id, AuthUtil.getUserId());
        return R.ok();
    }

    @Operation(summary = "获取项目列表")
    @GetMapping("")
    public R<List<ProjectDetail>> listAll() {
        return R.ok(projectService.listProject());
    }
}
