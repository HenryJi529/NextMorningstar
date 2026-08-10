package com.morningstar.dev.statemachine.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.properties.GitProperties;
import com.morningstar.dev.properties.SandboxProperties;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.FixResult;
import com.morningstar.dev.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FixAction extends AbstractAction {
    private final SandboxProperties sandboxProperties;
    private final RunMapper runMapper;
    private final IssueMapper issueMapper;
    private final ProcessUtil processUtil;
    private final ObjectMapper objectMapper;
    private final GitProperties gitProperties;

    public FixAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, SandboxProperties sandboxProperties, RunMapper runMapper, IssueMapper issueMapper, ProcessUtil processUtil, ObjectMapper objectMapper, GitProperties gitProperties) {
        super(stateMachineService, actionAttemptMapper, Event.FIX_SUCCEEDED, Event.FIX_FAILED);
        this.sandboxProperties = sandboxProperties;
        this.runMapper = runMapper;
        this.issueMapper = issueMapper;
        this.processUtil = processUtil;
        this.objectMapper = objectMapper;
        this.gitProperties = gitProperties;
    }

    @Override
    public Type getType() {
        return Type.FIX;
    }

    @Override
    protected FixResult doExecute(UUID runId) {
        // 解析信息
        Run run = runMapper.selectById(runId);
        String containerName = sandboxProperties.getContainerNamePrefix() + run.getId();
        String volumeName = sandboxProperties.getVolumeNamePrefix() + run.getProjectId();
        String fixBranchName = gitProperties.getFixBranchPrefix() + runId;

        int fixedSonarIssueNum = 0;
        int fixedAiIssueNum = 0;

        try {
            // 创建对应分支
            processUtil.run(
                    "docker", "run", "--rm",
                    "-v", volumeName + ":/workspace/repo",
                    "alpine/git",
                    "-c", "safe.directory=/workspace/repo",
                    "-C", "/workspace/repo",
                    "switch", "-C", fixBranchName);

            // 拿到对应的issue
            List<Issue> issues = issueMapper.selectList(
                    new LambdaQueryWrapper<Issue>()
                            .eq(Issue::getRunId, runId)
                            .eq(Issue::getStatus, Issue.Status.SELECTED)
            );

            for (Issue issue : issues) {
                // 构建提示词
                String prompt = buildFixPrompt(issue);

                // 提示词写入文件
                String script = "cat > /tmp/ai_fix_prompt.txt << 'EOF_PROMPT'\n"
                        + prompt + "\nEOF_PROMPT";
                processUtil.run("docker", "exec", containerName, "bash", "-c", script);

                // 运行修复命令
                String rawOutput = processUtil.run(
                        "docker", "exec",
                        "-w", "/workspace/repo",
                        containerName,
                        "bash", "-c",
                        "claude --dangerously-skip-permissions --print \"$(cat /tmp/ai_fix_prompt.txt)\"");
                log.info("AI修复原始输出: {}", rawOutput);

                // 解析输出
                Issue.CommitMessage commitMessage = objectMapper.readValue(
                        purifyLLMOutput(rawOutput),
                        Issue.CommitMessage.class
                );

                // 添加并提交修改
                processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "alpine/git",
                        "-c", "safe.directory=/workspace/repo",
                        "-C", "/workspace/repo",
                        "add", "-A");
                processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "alpine/git",
                        "-c", "safe.directory=/workspace/repo",
                        "-C", "/workspace/repo",
                        "commit",
                        "-m", commitMessage.getSubject(),
                        "-m", commitMessage.getBody());

                // 获取最新的commit sha
                String commitSha = processUtil.run(
                        "docker", "run", "--rm",
                        "-v", volumeName + ":/workspace/repo",
                        "alpine/git",
                        "-c", "safe.directory=/workspace/repo",
                        "-C", "/workspace/repo",
                        "rev-parse", "HEAD");

                // 更新issue
                issue.setCommitMessage(commitMessage);
                issue.setCommitSha(commitSha);
                issue.setStatus(Issue.Status.FIXED);
                issueMapper.updateById(issue);

                if (issue.getSource() == Issue.Source.SONAR) {
                    fixedSonarIssueNum++;
                } else {
                    fixedAiIssueNum++;
                }
            }

            return FixResult
                    .builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .fixedSonarIssueNum(fixedSonarIssueNum)
                    .fixedAiIssueNum(fixedAiIssueNum)
                    .build();
        } catch (ProcessUtil.ProcessExecutionException | JsonProcessingException e) {
            return FixResult
                    .builder()
                    .status(ActionResult.Status.FAILED)
                    .message(e.getMessage())
                    .fixedSonarIssueNum(fixedSonarIssueNum)
                    .fixedAiIssueNum(fixedAiIssueNum)
                    .build();
        }
    }

    private String buildFixPrompt(Issue issue) {
        return """
                你是一个代码修复专家。请根据以下信息，直接修改仓库中相关文件以修复该问题，修复完成后请输出 commit message。
                
                ## 问题信息
                - 标题: %s
                - 文件路径: %s
                - 问题描述: %s
                - 修复建议: %s
                - 代码片段: %s
                
                ## 修复要求
                - 直接修改文件完成修复，不要只给出修改建议。
                - 只修改与该问题直接相关的代码，不要重构无关部分、不要创建无关文件。
                - 尽力修复，即使问题复杂也不要放弃。
                
                ## 输出规则
                - 只输出以下格式JSON，不要任何额外文字或说明，不要用 Markdown 代码块，直接输出 JSON 本身。
                    ```json
                    {
                        "subject": "本次修复一句话总结，中文",
                        "body": "修复思路与具体改动，中文"
                    }
                    ```
                    - subject:
                        - 字段含义: 本次修复一句话总结（→ commit 第一行），这个字段值必须是中文
                    - body:
                        - 字段含义: 修复思路与具体改动（→ commit 正文），这个字段值必须是中文
                - JSON 字符串值内部的所有双引号必须转义，否则 JSON 解析失败。
                """.formatted(
                issue.getTitle(),
                issue.getMetadata().getFilePath(),
                issue.getMetadata().getDescription(),
                issue.getMetadata().getSuggestion(),
                issue.getMetadata().getCodeSnippet());
    }

    private String purifyLLMOutput(String rawOutput) {
        int start = rawOutput.indexOf('{');
        int end = rawOutput.lastIndexOf('}');
        if (start == -1 || end == -1 || start >= end) {
            return ""; // 触发JSON解析失败
        }
        return rawOutput.substring(start, end + 1).trim();
    }

}
