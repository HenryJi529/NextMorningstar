package com.morningstar.dev.statemachine.action;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.bo.RepoIdentity;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.properties.GiteaProperties;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.SyncResult;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.dev.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class SyncAction extends AbstractAction {
    private final ProcessUtil processUtil;
    private final RunMapper runMapper;
    private final ProjectMapper projectMapper;
    private final GiteaUtil giteaUtil;
    private final GiteaProperties giteaProperties;
    private final CommonSteps commonSteps;


    public SyncAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, ProcessUtil processUtil, RunMapper runMapper, ProjectMapper projectMapper, GiteaUtil giteaUtil, GiteaProperties giteaProperties, CommonSteps commonSteps) {
        super(stateMachineService, actionAttemptMapper, Event.SYNC_SUCCEEDED, Event.SYNC_FAILED);
        this.processUtil = processUtil;
        this.runMapper = runMapper;
        this.projectMapper = projectMapper;
        this.giteaUtil = giteaUtil;
        this.giteaProperties = giteaProperties;
        this.commonSteps = commonSteps;
    }

    @Override
    public Action.Type getType() {
        return Action.Type.SYNC;
    }

    @Override
    protected ActionResult doExecute(UUID runId) {
        // 解析信息
        Run run = runMapper.selectById(runId);
        Project project = projectMapper.selectById(run.getProjectId());
        RepoIdentity repoIdentity = giteaUtil.parseRepoIdentity(project.getLink());
        String branchName = project.getBranchName();
        String volumeName = commonSteps.getVolumeName(run);
        String containerName = commonSteps.getContainerName(run);
        String gitUrl = giteaProperties.getContainerOrigin() + "/" + repoIdentity.getOwnerName() + "/" + repoIdentity.getRepoName() + ".git";


        try {
            // 判断是否是首次 clone
            boolean isFirstTime = !processUtil.test(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "rev-parse", "--is-inside-work-tree"
            );

            if (isFirstTime) {
                // 清空目录(alpine/git 不带 shell,用 alpine 基础镜像跑 rm)
                processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "alpine",
                        "find", "/workspace/repo", "-mindepth", "1", "-delete");

                // 克隆仓库
                processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "--add-host", "host.docker.internal:host-gateway",
                        "alpine/git",
                        "-c", "http.extraHeader=Authorization: token " + giteaProperties.getBotToken(),
                        "clone", "--branch", branchName,
                        gitUrl, "/workspace/repo");
            } else {
                // 更新仓库
                processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "--add-host", "host.docker.internal:host-gateway",
                        "alpine/git",
                        "-c", "safe.directory=/workspace/repo",
                        "-C", "/workspace/repo",
                        "-c", "http.extraHeader=Authorization: token " + giteaProperties.getBotToken(),
                        "fetch", "origin", branchName);
                // 切换分支
                processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "alpine/git",
                        "-c", "safe.directory=/workspace/repo",
                        "-C", "/workspace/repo",
                        "switch", "-C", branchName, "origin/" + branchName);
                // 把被 git 忽略和未跟踪的文件删除干净
                processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "alpine/git",
                        "-c", "safe.directory=/workspace/repo",
                        "-C", "/workspace/repo",
                        "clean", "-fdx");
            }

            // 配置 commit 需要的用户名和邮箱
            processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "config", "user.name", giteaProperties.getBotUsername());
            processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "config", "user.email", giteaProperties.getBotEmail());

            // 获取最新的 commit sha
            String commitSha = commonSteps.getHeadCommitSha(run);

            // 修正属主:alpine/git 以 root 创建的文件 bot 无法写
            processUtil.run(
                    "docker", "exec",
                    "--user", "root",
                    containerName,
                    "chown", "-R", "bot:bot", "/workspace/repo");

            return SyncResult
                    .builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .gitUrl(gitUrl)
                    .branchName(branchName)
                    .commitSha(commitSha)
                    .build();
        } catch (ProcessUtil.ProcessExecutionException e) {
            return SyncResult.builder()
                    .status(ActionResult.Status.FAILED)
                    .gitUrl(gitUrl)
                    .branchName(branchName)
                    .message(e.getMessage())
                    .build();
        }
    }
}
