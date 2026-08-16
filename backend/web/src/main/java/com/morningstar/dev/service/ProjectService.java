package com.morningstar.dev.service;

import com.morningstar.dev.pojo.bo.ProjectDetail;
import com.morningstar.dev.pojo.vo.CreateProjectRequestVo;
import com.morningstar.dev.pojo.vo.UpdateProjectRequestVo;
import com.morningstar.infra.response.PageResult;

import java.util.UUID;

public interface ProjectService {
    ProjectDetail createProject(CreateProjectRequestVo vo);

    ProjectDetail updateProject(UpdateProjectRequestVo vo);

    void deleteProject(UUID projectId, UUID adminId);

    ProjectDetail getProjectById(UUID projectId);

    PageResult<ProjectDetail> listProject(int pageNum, int pageSize);
}
