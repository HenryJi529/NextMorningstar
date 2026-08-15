package com.morningstar.dev.service;

import com.morningstar.dev.pojo.bo.ProjectDetail;
import com.morningstar.dev.pojo.vo.CreateProjectRequestVo;
import com.morningstar.dev.pojo.vo.UpdateProjectRequestVo;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectDetail createProject(CreateProjectRequestVo vo);

    ProjectDetail updateProject(UpdateProjectRequestVo vo);

    void deleteProject(UUID projectId, UUID adminId);

    ProjectDetail getProjectById(UUID projectId);

    List<ProjectDetail> listProject();
}
