package com.morningstar.dev.service;

import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.vo.CreateProjectRequestVo;
import com.morningstar.dev.pojo.vo.UpdateProjectRequestVo;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    Project createProject(CreateProjectRequestVo vo);

    Project updateProject(UpdateProjectRequestVo vo);

    void deleteProject(UUID projectId, UUID adminId);

    Project getProjectById(UUID projectId);

    List<Project> getAllProject();
}
