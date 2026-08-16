package com.morningstar.dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.pojo.bo.ProjectDetail;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.vo.CreateProjectRequestVo;
import com.morningstar.dev.pojo.vo.UpdateProjectRequestVo;
import com.morningstar.dev.properties.MaxIssuesPerRunProperties;
import com.morningstar.dev.service.ProjectService;
import com.morningstar.dev.service.RunService;
import com.morningstar.dev.statemachine.action.CommonSteps;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.dev.util.SonarUtil;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.PageResult;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.infra.util.CopyUtil;
import com.morningstar.system.dao.mapper.UserMapper;
import com.morningstar.system.pojo.po.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final GiteaUtil giteaUtil;
    private final MaxIssuesPerRunProperties maxIssuesPerRunProperties;
    private final SonarUtil sonarUtil;
    private final CommonSteps commonSteps;
    private final RunService runService;
    private final UserMapper userMapper;

    @Override
    public ProjectDetail createProject(CreateProjectRequestVo vo) {
        giteaUtil.validateRepoAndBranch(vo.getLink(), vo.getBranchName());

        giteaUtil.addCollaborator(vo.getLink());

        Project project = Project.builder()
                .id(UUID.randomUUID())
                .adminId(vo.getAdminId())
                .name(vo.getName())
                .link(giteaUtil.formatRepoLink(vo.getLink()))
                .branchName(vo.getBranchName())
                .description(vo.getDescription())
                .maxSonarIssuesPerRun(Optional.ofNullable(vo.getMaxSonarIssuesPerRun()).orElse(maxIssuesPerRunProperties.getSonar()))
                .maxAiIssuesPerRun(Optional.ofNullable(vo.getMaxAiIssuesPerRun()).orElse(maxIssuesPerRunProperties.getAi()))
                .enabled(true)
                .build();

        try {
            projectMapper.insert(project);
        } catch (DuplicateKeyException e) {
            throw new BaseException(ResponseCode.DEV_PROJECT_LINK_DUPLICATE, vo.getLink());
        }
        return toDetail(project);
    }

    @Override
    public ProjectDetail updateProject(UpdateProjectRequestVo vo) {
        Project dbProject = projectMapper.selectById(vo.getId());
        if (dbProject == null) {
            throw new BaseException(ResponseCode.DEV_PROJECT_NOT_FOUND, vo.getId());
        }

        if (!dbProject.getAdminId().equals(vo.getAdminId())) {
            throw new BaseException(ResponseCode.DEV_PROJECT_ACCESS_DENIED, vo.getId());
        }

        if (vo.getBranchName() != null) {
            giteaUtil.validateRepoAndBranch(dbProject.getLink(), vo.getBranchName());
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
                .maxSonarIssuesPerRun(vo.getMaxSonarIssuesPerRun())
                .maxAiIssuesPerRun(vo.getMaxAiIssuesPerRun())
                .enabled(vo.getEnabled())
                .build());

        if (disabling) {
            giteaUtil.removeCollaborator(dbProject.getLink());
        }

        return toDetail(projectMapper.selectById(vo.getId()));
    }

    @Override
    public void deleteProject(UUID projectId, UUID adminId) {
        Project dbProject = projectMapper.selectById(projectId);
        if (dbProject == null) {
            throw new BaseException(ResponseCode.DEV_PROJECT_NOT_FOUND, projectId);
        }
        if (!dbProject.getAdminId().equals(adminId)) {
            throw new BaseException(ResponseCode.DEV_PROJECT_ACCESS_DENIED, projectId);
        }

        // 确保没有正在运行的 run
        if (runService.hasActiveRun(projectId)) {
            throw new BaseException(ResponseCode.DEV_PROJECT_HAS_ACTIVE_RUN, projectId);
        }

        // 删除 SonarQube 项目(幂等)
        try {
            sonarUtil.deleteSonarProjectByKey(commonSteps.getSonarProjectKey(dbProject));
        } catch (RestClientException e) {
            if (!e.getMessage().contains("not found")) {
                throw new BaseException(e.getMessage());
            }
        }

        // 删除 Bot 的仓库权限
        giteaUtil.removeCollaborator(dbProject.getLink());

        // 确保用户可以重试
        projectMapper.deleteById(projectId);
    }

    @Override
    public ProjectDetail getProjectById(UUID projectId) {
        Project dbProject = projectMapper.selectById(projectId);
        if (dbProject == null) {
            throw new BaseException(ResponseCode.DEV_PROJECT_NOT_FOUND, projectId);
        }
        return toDetail(dbProject);
    }

    @Override
    public PageResult<ProjectDetail> listProject(int pageNum, int pageSize) {
        Page<Project> page = projectMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Project>().orderByDesc(Project::getCreateTime));
        return new PageResult<>(toDetails(page.getRecords()), pageNum, pageSize, page.getTotal());
    }

    private ProjectDetail toDetail(Project project) {
        ProjectDetail detail = new ProjectDetail();
        CopyUtil.copyNonNullProperties(project, detail);
        User admin = userMapper.selectById(project.getAdminId());
        if (admin != null) {
            detail.setAdminName(admin.getUsername());
        }
        return detail;
    }

    private List<ProjectDetail> toDetails(List<Project> projects) {
        return projects.stream().map(this::toDetail).toList();
    }
}
