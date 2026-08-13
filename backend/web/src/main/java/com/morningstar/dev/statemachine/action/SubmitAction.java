package com.morningstar.dev.statemachine.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.properties.GitProperties;
import com.morningstar.dev.properties.GiteaProperties;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.SubmitResult;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.dev.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubmitAction extends AbstractAction {
    private final ProcessUtil processUtil;
    private final CommonSteps commonSteps;
    private final RunMapper runMapper;
    private final IssueMapper issueMapper;
    private final ProjectMapper projectMapper;
    private final GitProperties gitProperties;
    private final GiteaProperties giteaProperties;
    private final GiteaUtil giteaUtil;

    public SubmitAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, ProcessUtil processUtil, CommonSteps commonSteps, RunMapper runMapper, IssueMapper issueMapper, ProjectMapper projectMapper, GitProperties gitProperties, GiteaProperties giteaProperties, GiteaUtil giteaUtil) {
        super(stateMachineService, actionAttemptMapper, Event.SUBMIT_SUCCEEDED, Event.SUBMIT_FAILED);
        this.processUtil = processUtil;
        this.commonSteps = commonSteps;
        this.runMapper = runMapper;
        this.issueMapper = issueMapper;
        this.projectMapper = projectMapper;
        this.gitProperties = gitProperties;
        this.giteaProperties = giteaProperties;
        this.giteaUtil = giteaUtil;
    }

    @Override
    public Action.Type getType() {
        return Action.Type.SUBMIT;
    }

    @Override
    protected SubmitResult doExecute(UUID runId) {
        // 解析信息
        Run run = runMapper.selectById(runId);
        Project project = projectMapper.selectById(run.getProjectId());
        String volumeName = commonSteps.getVolumeName(run);
        String fixBranchName = gitProperties.getFixBranchPrefix() + runId;

        try {
            // git push
            processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "--add-host", "host.docker.internal:host-gateway",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "-c", "http.extraHeader=Authorization: token " + giteaProperties.getBotToken(),
                    "push", "origin", fixBranchName);

            // 发起 PR
            List<Issue> verifiedIssues = issueMapper.selectList(
                    new LambdaQueryWrapper<Issue>()
                            .eq(Issue::getRunId, runId)
                            .eq(Issue::getStatus, Issue.Status.VERIFIED));

            String title = "AI 代码质量优化 " + verifiedIssues.size() + " 项";
            String body = buildPrBody(verifiedIssues, project);

            GiteaUtil.PullRequest pr = giteaUtil.createPullRequest(
                    project.getLink(),
                    fixBranchName, project.getBranchName(),
                    title, body);

            // 回写 Run
            run.setPrId(pr.getNumber());
            run.setPrStatus(Run.PrStatus.OPEN);
            runMapper.updateById(run);

            return SubmitResult.builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .prUrl(pr.getHtmlUrl())
                    .prTitle(title)
                    .prBody(body)
                    .build();
        } catch (ProcessUtil.ProcessExecutionException | RestClientException e) {
            return SubmitResult
                    .builder()
                    .status(ActionResult.Status.FAILED)
                    .message(e.getMessage())
                    .build();
        }
    }

    private String buildPrBody(List<Issue> issues, Project project) {
        String issueSections = issues.stream().map(issue -> {

            String commitUrl = giteaUtil.getCommitLink(project.getLink(), issue.getCommitSha());

            if (issue.getSource() == Issue.Source.SONAR) {
                return """
                        1. %s【来源：%s】
                            - 问题级别：可靠性 %s | 安全性 %s | 可维护性 %s
                            - 代码片段：[%s:L%s-L%s](%s)
                            - 修改记录: [%s](%s)
                        """.formatted(
                        issue.getTitle(),
                        issue.getSource(),
                        issue.getReliabilitySeverity() != null ? issue.getReliabilitySeverity() : "N/A",
                        issue.getSecuritySeverity() != null ? issue.getSecuritySeverity() : "N/A",
                        issue.getMaintainabilitySeverity() != null ? issue.getMaintainabilitySeverity() : "N/A",
                        issue.getMetadata().getFilePath(),
                        issue.getMetadata().getStartLine(),
                        issue.getMetadata().getEndLine(),
                        giteaUtil.getCodeSnippetLink(issue.getMetadata().getFilePath(), issue.getMetadata().getStartLine(), issue.getMetadata().getEndLine(), project.getLink(), issue.getCommitSha()),
                        issue.getCommitSha(),
                        commitUrl
                );
            } else {
                return """
                        1. %s【来源：%s】
                            - 问题类型：%s(%s)
                            - 问题级别：可靠性 %s | 安全性 %s | 可维护性 %s
                            - 代码片段：[%s:L%s-L%s](%s)
                            - 问题描述：%s
                            - 修改记录: [%s](%s)
                        """.formatted(
                        issue.getTitle(),
                        issue.getSource(),
                        ((Issue.AiMetadata) issue.getMetadata()).getType().getName(),
                        ((Issue.AiMetadata) issue.getMetadata()).getType().getDescription(),
                        issue.getReliabilitySeverity() != null ? issue.getReliabilitySeverity() : "N/A",
                        issue.getSecuritySeverity() != null ? issue.getSecuritySeverity() : "N/A",
                        issue.getMaintainabilitySeverity() != null ? issue.getMaintainabilitySeverity() : "N/A",
                        issue.getMetadata().getFilePath(),
                        issue.getMetadata().getStartLine(),
                        issue.getMetadata().getEndLine(),
                        giteaUtil.getCodeSnippetLink(issue.getMetadata().getFilePath(), issue.getMetadata().getStartLine(), issue.getMetadata().getEndLine(), project.getLink(), issue.getCommitSha()),
                        issue.getMetadata().getDescription(),
                        issue.getCommitSha(),
                        commitUrl
                );
            }
        }).collect(Collectors.joining("\n\n"));

        return """
                # 🤖 代码质量优化报告
                
                本 PR 由 **Haibara Ai** 自动生成，共修复 **%s** 个问题。
                
                ⚠️ AI 无主干合并权限，请人工 review 后决定是否合并。
                
                ___
                
                %s
                """.formatted(
                issues.size(),
                issueSections
        );
    }

}
