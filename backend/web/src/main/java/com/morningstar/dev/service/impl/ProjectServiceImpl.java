package com.morningstar.dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.vo.CreateProjectRequestVo;
import com.morningstar.dev.pojo.vo.UpdateProjectRequestVo;
import com.morningstar.dev.service.ProjectService;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final GiteaUtil giteaUtil;

    @Override
    public Project createProject(CreateProjectRequestVo vo) {
        giteaUtil.addCollaborator(vo.getLink());

        Project project = Project.builder()
                .id(UUID.randomUUID())
                .adminId(vo.getAdminId())
                .name(vo.getName())
                .link(giteaUtil.formatRepoLink(vo.getLink()))
                .branchName(vo.getBranchName())
                .description(vo.getDescription())
                .maxFixesPerRun(vo.getMaxFixesPerRun())
                .enabled(true)
                .build();

        try {
            projectMapper.insert(project);
        } catch (DuplicateKeyException e) {
            throw new BaseException(ResponseCode.DEV_PROJECT_LINK_DUPLICATE, vo.getLink());
        }
        return project;
    }

    @Override
    public Project updateProject(UpdateProjectRequestVo vo) {
        Project dbProject = projectMapper.selectById(vo.getId());
        if (dbProject == null) {
            throw new BaseException(ResponseCode.DEV_PROJECT_NOT_FOUND, vo.getId());
        }

        if (!dbProject.getAdminId().equals(vo.getAdminId())) {
            throw new BaseException(ResponseCode.DEV_PROJECT_ACCESS_DENIED, vo.getId());
        }

        boolean enabling = Boolean.TRUE.equals(vo.getEnabled()) && !Boolean.TRUE.equals(dbProject.getEnabled());
        boolean disabling = Boolean.FALSE.equals(vo.getEnabled()) && Boolean.TRUE.equals(dbProject.getEnabled());

        if (enabling) {
            giteaUtil.addCollaborator(dbProject.getLink());
        }

        projectMapper.updateById(Project.builder()
                .id(vo.getId())
                .name(vo.getName())
                .branchName(vo.getBranchName())
                .description(vo.getDescription())
                .maxFixesPerRun(vo.getMaxFixesPerRun())
                .enabled(vo.getEnabled())
                .build());

        if (disabling) {
            giteaUtil.removeCollaborator(dbProject.getLink());
        }

        return projectMapper.selectById(vo.getId());
    }

    @Override
    public void deleteProject(UUID projectId, UUID adminId) {
        Project dbProject = getProjectById(projectId, adminId);

        projectMapper.deleteById(projectId);

        giteaUtil.removeCollaborator(dbProject.getLink());
    }

    @Override
    public Project getProjectById(UUID projectId, UUID adminId) {
        Project dbProject = projectMapper.selectById(projectId);
        if (dbProject == null) {
            throw new BaseException(ResponseCode.DEV_PROJECT_NOT_FOUND, projectId);
        }
        if (!dbProject.getAdminId().equals(adminId)) {
            throw new BaseException(ResponseCode.DEV_PROJECT_ACCESS_DENIED, projectId);
        }
        return dbProject;
    }

    @Override
    public List<Project> getAllProjectByAdminId(UUID adminId) {
        return projectMapper.selectList(
                new LambdaQueryWrapper<Project>().eq(Project::getAdminId, adminId)
        );
    }
}
