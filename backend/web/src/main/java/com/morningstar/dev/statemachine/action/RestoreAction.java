package com.morningstar.dev.statemachine.action;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.properties.GitProperties;
import com.morningstar.dev.properties.SandboxProperties;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.RestoreResult;
import com.morningstar.dev.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class RestoreAction extends AbstractAction {
    private final ProcessUtil processUtil;
    private final RunMapper runMapper;
    private final ProjectMapper projectMapper;
    private final SandboxProperties sandboxProperties;
    private final IssueMapper issueMapper;
    private final GitProperties gitProperties;

    public RestoreAction(StateMachineService stateMachineService,
                         ActionAttemptMapper actionAttemptMapper,
                         ProcessUtil processUtil,
                         RunMapper runMapper,
                         ProjectMapper projectMapper,
                         SandboxProperties sandboxProperties, IssueMapper issueMapper, GitProperties gitProperties) {
        super(stateMachineService, actionAttemptMapper, Event.RESTORE_SUCCEEDED, Event.RESTORE_FAILED);
        this.processUtil = processUtil;
        this.runMapper = runMapper;
        this.projectMapper = projectMapper;
        this.sandboxProperties = sandboxProperties;
        this.issueMapper = issueMapper;
        this.gitProperties = gitProperties;
    }

    @Override
    public Action.Type getType() {
        return Type.RESTORE;
    }

    @Override
    protected ActionResult doExecute(UUID runId) {
        Run run = runMapper.selectById(runId);
        Project project = projectMapper.selectById(run.getProjectId());
        String branchName = project.getBranchName();
        String volumeName = sandboxProperties.getVolumeNamePrefix() + run.getProjectId();
        String containerName = sandboxProperties.getContainerNamePrefix() + run.getId();
        String fixBranchName = gitProperties.getFixBranchPrefix() + runId;

        try {
            // 还原 issues
            issueMapper.update(null,
                    new LambdaUpdateWrapper<Issue>()
                            .eq(Issue::getRunId, runId)
                            .in(Issue::getStatus, Issue.Status.FIXED, Issue.Status.VERIFIED)
                            .set(Issue::getStatus, Issue.Status.SELECTED)
                            .set(Issue::getCommitSha, null)
                            .set(Issue::getCommitMessage, null));

            // 丢弃 fix 分支上已跟踪文件的修改
            processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "reset", "--hard", "HEAD");

            // 删除 untracked 文件和目录
            processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "clean", "-fdx");

            // 切回配置的原始分支
            processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "switch", branchName);

            // 删除 fix 分支(如果存在)
            if (processUtil.test(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "rev-parse", "--verify", fixBranchName)) {
                processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "alpine/git",
                        "-c", "safe.directory=/workspace/repo",
                        "-C", "/workspace/repo",
                        "branch", "-D", fixBranchName);
            }

            // 重置原始分支到 fetch 状态
            processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "reset", "--hard", "origin/" + branchName);

            // 获取最新的 commit sha
            String commitSha = processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "rev-parse", "HEAD");

            // 修正属主
            processUtil.run(
                    "docker", "exec",
                    "--user", "root",
                    containerName,
                    "chown", "-R", "bot:bot", "/workspace/repo");

            return RestoreResult.builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .commitSha(commitSha)
                    .build();
        } catch (ProcessUtil.ProcessExecutionException e) {
            return RestoreResult.builder()
                    .status(ActionResult.Status.FAILED)
                    .message(e.getMessage())
                    .build();
        }
    }
}
