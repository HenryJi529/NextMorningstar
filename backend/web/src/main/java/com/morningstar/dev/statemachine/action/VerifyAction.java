package com.morningstar.dev.statemachine.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.ScanResult;
import com.morningstar.dev.statemachine.result.VerifyResult;
import com.morningstar.dev.util.ProcessUtil;
import com.morningstar.dev.util.SonarUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VerifyAction extends AbstractAction {
    private final RunMapper runMapper;
    private final ProjectMapper projectMapper;
    private final CommonSteps commonSteps;
    private final IssueMapper issueMapper;

    public VerifyAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, RunMapper runMapper, ProjectMapper projectMapper, CommonSteps commonSteps, IssueMapper issueMapper) {
        super(stateMachineService, actionAttemptMapper, Event.VERIFY_SUCCEEDED, Event.VERIFY_FAILED);
        this.runMapper = runMapper;
        this.projectMapper = projectMapper;
        this.commonSteps = commonSteps;
        this.issueMapper = issueMapper;
    }

    @Override
    public Action.Type getType() {
        return Action.Type.VERIFY;
    }

    @Override
    protected VerifyResult doExecute(UUID runId) {
        // 解析信息
        Run run = runMapper.selectById(runId);
        Project project = projectMapper.selectById(run.getProjectId());
        int verifiedAiIssueNum = 0;
        int verifiedSonarIssueNum = 0;

        try {
            // Maven 构建
            commonSteps.mavenBuild(run);

            // Sonar 扫描
            List<SonarUtil.SonarIssue> sonarIssues = commonSteps.sonarScan(run, project);
            List<String> currentSonarIssueKeys = sonarIssues.stream().map(SonarUtil.SonarIssue::getKey).toList();

            // 获取上次的Sonar扫描结果
            ScanResult lastScanResult = (ScanResult) getActionAttemptMapper().selectLatestActionAttempt(runId, Action.Type.SCAN).getResult();

            // 获取本轮修复的SonarIssue
            List<String> fixedSonarIssueKeys = issueMapper.selectList(
                            new LambdaQueryWrapper<Issue>()
                                    .eq(Issue::getRunId, runId)
                                    .eq(Issue::getStatus, Issue.Status.FIXED)
                                    .eq(Issue::getSource, Issue.Source.SONAR)
                    )
                    .stream()
                    .map(issue -> ((Issue.SonarMetadata) issue.getMetadata()).getIssueKey())
                    .toList();

            // 是否有未被修复的问题
            boolean hasUnfixed = fixedSonarIssueKeys.stream()
                    .anyMatch(currentSonarIssueKeys::contains);
            log.info("是否有未被修复的问题: {}", hasUnfixed);
            // 是否引入了新问题  NOTE: 存在修复更多旧issue的同时又引入新issue的可能
            boolean hasRegression = currentSonarIssueKeys.size()
                    > lastScanResult.getScannedSonarIssueNum() - fixedSonarIssueKeys.size();
            log.info("是否引入了新问题: {}", hasRegression);
            if (hasUnfixed || hasRegression) {
                return VerifyResult
                        .builder()
                        .status(ActionResult.Status.FAILED)
                        .message("有未被修复的问题 或 引入了新问题")
                        .build();
            }

            List<Issue> fixedIssues = issueMapper.selectList(
                    new LambdaQueryWrapper<Issue>()
                            .eq(Issue::getRunId, runId)
                            .eq(Issue::getStatus, Issue.Status.FIXED)
            );

            for (Issue fixedIssue : fixedIssues) {
                boolean isVerified = commonSteps.runClaude(
                        CommonSteps.ClaudeInput
                                .builder()
                                .intent(CommonSteps.ClaudeInput.Intent.VERIFY)
                                .prompt(buildVerifyPrompt(fixedIssue))
                                .build(),
                        run,
                        VerifyVerdict.class
                ).isVerified();
                if (isVerified) {
                    if (fixedIssue.getSource() == Issue.Source.SONAR) {
                        verifiedSonarIssueNum++;
                    } else {
                        verifiedAiIssueNum++;
                    }
                    fixedIssue.setStatus(Issue.Status.VERIFIED);
                    issueMapper.updateById(fixedIssue);
                } else {
                    return VerifyResult
                            .builder()
                            .status(ActionResult.Status.FAILED)
                            .message(String.format("问题%s未通过AI验证", fixedIssue.getId()))
                            .build();
                }
            }

            return VerifyResult
                    .builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .verifiedSonarIssueNum(verifiedSonarIssueNum)
                    .verifiedAiIssueNum(verifiedAiIssueNum)
                    .build();
        } catch (ProcessUtil.ProcessExecutionException | JsonProcessingException e) {
            return VerifyResult
                    .builder()
                    .status(ActionResult.Status.FAILED)
                    .verifiedSonarIssueNum(verifiedSonarIssueNum)
                    .verifiedAiIssueNum(verifiedAiIssueNum)
                    .message(e.getMessage())
                    .build();
        }
    }

    private String buildVerifyPrompt(Issue issue) {
        Issue.CommitMessage commitMessage = issue.getCommitMessage();
        return """
                你是一个代码验证专家。请验证前一个 AI 对以下问题的修复是否正确。
                
                ## 问题信息(扫描阶段发现)
                - 标题: %s
                - 文件路径: %s(仓库内相对路径,请直接读取该文件查看修复后的代码)
                - 问题描述: %s
                - 修复建议: %s
                - 原始代码片段(问题代码): %s
                
                ## 前一个 AI 的修复说明
                - 一句话总结: %s
                - 修复思路与具体改动: %s
                
                ## 验证要求
                请读取上述文件路径的当前代码,从两个维度验证:
                
                1. 思路是否正确：前一个 AI 描述的修复思路，逻辑上能否真正解决上述问题?
                2. 实现是否到位：当前代码是否确实按该思路做了正确修改，且真正消除了原始问题?
                
                ## 注意事项
                - 不要只看修复说明写得是否合理,必须对照实际代码确认改动真实存在且正确。
                - 若改动属于表面应付(如吞掉异常、注释掉问题代码、留空实现)、偏离所述思路、或未真正消除问题，一律判为未通过。
                
                ## 输出规则
                - 只输出以下格式JSON，不要任何额外文字或说明,不要用 Markdown 代码块，直接输出 JSON 本身。
                    ```json
                    {
                        "verified": true 或 false
                    }
                    ```
                """.formatted(
                issue.getTitle(),
                issue.getMetadata().getFilePath(),
                issue.getMetadata().getDescription(),
                issue.getMetadata().getSuggestion(),
                issue.getMetadata().getCodeSnippet(),
                commitMessage.getSubject(),
                commitMessage.getBody());
    }

    @Data
    private static class VerifyVerdict {
        private boolean verified;
    }
}
