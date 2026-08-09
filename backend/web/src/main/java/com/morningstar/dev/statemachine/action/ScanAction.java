package com.morningstar.dev.statemachine.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.bo.AiIssue;
import com.morningstar.dev.pojo.bo.RepoIdentity;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.properties.SandboxProperties;
import com.morningstar.dev.properties.SonarqubeProperties;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.ScanResult;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.dev.util.ProcessUtil;
import com.morningstar.dev.util.SonarUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScanAction extends AbstractAction {
    private final ProcessUtil processUtil;
    private final RunMapper runMapper;
    private final ProjectMapper projectMapper;
    private final SandboxProperties sandboxProperties;
    private final GiteaUtil giteaUtil;
    private final SonarqubeProperties sonarqubeProperties;
    private final SonarUtil sonarUtil;
    private final ObjectMapper objectMapper;
    private final IssueMapper issueMapper;

    public ScanAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, ProcessUtil processUtil, RunMapper runMapper, ProjectMapper projectMapper, SandboxProperties sandboxProperties, GiteaUtil giteaUtil, SonarqubeProperties sonarqubeProperties, SonarUtil sonarUtil, ObjectMapper objectMapper, IssueMapper issueMapper) {
        super(stateMachineService, actionAttemptMapper, Event.SCAN_SUCCEEDED, Event.SCAN_FAILED);
        this.processUtil = processUtil;
        this.runMapper = runMapper;
        this.projectMapper = projectMapper;
        this.sandboxProperties = sandboxProperties;
        this.giteaUtil = giteaUtil;
        this.sonarqubeProperties = sonarqubeProperties;
        this.sonarUtil = sonarUtil;
        this.objectMapper = objectMapper;
        this.issueMapper = issueMapper;
    }

    @Override
    public Action.Type getType() {
        return Action.Type.SCAN;
    }

    @Override
    protected ScanResult doExecute(UUID runId) {
        // 解析信息
        Run run = runMapper.selectById(runId);
        Project project = projectMapper.selectById(run.getProjectId());
        String containerName = sandboxProperties.getContainerNamePrefix() + run.getId();
        RepoIdentity repoIdentity = giteaUtil.parseRepoIdentity(project.getLink());

        try {
            // Maven 构建
            processUtil.run(
                    "docker", "exec", containerName, "bash", "-c",
                    "p=$(find /workspace/repo -name pom.xml | head -1) && [ \"$p\" ] && mvn -s /workspace/maven-settings.xml -q compile -f \"$p\" || true"
            );

            // Sonar 扫描
            String sonarProjectKey = repoIdentity.getOwnerName() + ":" + repoIdentity.getRepoName();
            String sonarProjectName = project.getName();

            processUtil.run(
                    "docker", "exec", "-w", "/workspace/repo", containerName,
                    "sonar-scanner",
                    "-Dsonar.projectKey=" + sonarProjectKey,
                    "-Dsonar.projectName=" + sonarProjectName,
                    "-Dsonar.sources=.",
                    "-Dsonar.java.binaries=**/target/classes",
                    "-Dsonar.exclusions=**/target/**/*.jar,**/node_modules/**",
                    "-Dsonar.scm.disabled=true",
                    "-Dsonar.working.directory=/tmp/.scannerwork",
                    "-Dsonar.host.url=" + sonarqubeProperties.getContainerOrigin(),
                    "-Dsonar.token=" + sonarqubeProperties.getToken()
            );
            List<SonarUtil.SonarIssue> sonarIssues = sonarUtil.getAllOpenSonarIssuesByProjectKey(sonarProjectKey);
            List<String> sonarIssueKeys = sonarIssues.stream().map(SonarUtil.SonarIssue::getKey).collect(Collectors.toList());
            if (!sonarIssues.isEmpty()) {
                log.info("Sonar Issue典型对象: {}", sonarIssues.get(0));
            }

            // AI 扫描
            String prompt = buildAiScanPrompt();
            String script = "cat > /tmp/ai_scan_prompt.txt << 'EOF_PROMPT'\n"
                    + prompt + "\nEOF_PROMPT";
            processUtil.run("docker", "exec", containerName, "bash", "-c", script);

            String rawOutput = processUtil.run(
                    "docker", "exec",
                    "-w", "/workspace/repo",
                    containerName,
                    "bash", "-c",
                    "claude --dangerously-skip-permissions --print \"$(cat /tmp/ai_scan_prompt.txt)\"");
            log.info("AI扫描原始输出: {}", rawOutput);

            AiIssue[] aiIssues = objectMapper.readValue(
                    purifyLLMOutput(rawOutput),
                    AiIssue[].class
            );
            if (aiIssues.length > 0) {
                log.info("AI Issue典型对象: {}", aiIssues[0]);
            }

            // 选择 Sonar 和 AI 生成的 Issue, 转换并落表
            List<Issue> issues = new ArrayList<>();

            List<SonarUtil.SonarIssue> selectedSonarIssues = new ArrayList<>(sonarIssues);
            Collections.shuffle(selectedSonarIssues);
            if (selectedSonarIssues.size() > project.getMaxSonarIssuesPerRun()) {
                selectedSonarIssues = selectedSonarIssues.subList(0, project.getMaxSonarIssuesPerRun());
            }

            issues.addAll(selectedSonarIssues.stream().map(sonarIssue -> convertSonarIssueToIssue(sonarIssue, containerName, runId)).toList());
            List<AiIssue> selectedAiIssues = new ArrayList<>(Arrays.asList(aiIssues));
            Collections.shuffle(selectedAiIssues);
            if (aiIssues.length > project.getMaxAiIssuesPerRun()) {
                selectedAiIssues = selectedAiIssues.subList(0, project.getMaxAiIssuesPerRun());
            }
            issues.addAll(selectedAiIssues.stream().map(aiIssue -> convertAiIssueToIssue(aiIssue, runId)).toList());

            issueMapper.insert(issues);

            return ScanResult
                    .builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .sonarIssueNum(sonarIssues.size())
                    .aiIssueNum(aiIssues.length)
                    .sonarIssueKeys(sonarIssueKeys)
                    .build();
        } catch (ProcessUtil.ProcessExecutionException | JsonProcessingException e) {
            return ScanResult
                    .builder()
                    .status(ActionResult.Status.FAILED)
                    .message(e.getMessage())
                    .build();
        }
    }

    private Issue convertAiIssueToIssue(AiIssue aiIssue, UUID runId) {
        return Issue
                .builder()
                .runId(runId)
                .source(Issue.Source.AI)
                .metadata(Issue.AiMetadata
                        .builder()
                        .description(aiIssue.getDescription())
                        .suggestion(aiIssue.getSuggestion())
                        .filePath(aiIssue.getFilePath())
                        .codeSnippet(aiIssue.getCodeSnippet())
                        .type(aiIssue.getType())
                        .build()
                )
                .title(aiIssue.getTitle())
                .reliabilitySeverity(aiIssue.getReliabilitySeverity())
                .securitySeverity(aiIssue.getSecuritySeverity())
                .maintainabilitySeverity(aiIssue.getMaintainabilitySeverity())
                .effortInMinutes(aiIssue.getEffortInMinutes())
                .status(Issue.Status.SELECTED)
                .build();
    }

    private Issue convertSonarIssueToIssue(SonarUtil.SonarIssue sonarIssue, String containerName, UUID runId) {
        String filePath = getFilePathFromSonarIssue(sonarIssue);
        String codeSnippet = getCodeSnippetFromSonarIssue(sonarIssue, containerName);

        SonarUtil.SonarRule sonarRule = sonarUtil.getSonarRuleByKey(sonarIssue.getRule());

        String introduction = getDescriptionSectionFromSonarRule(sonarRule, SonarUtil.SonarRule.DescriptionSection.Key.introduction).getContent();
        String how_to_fix = getDescriptionSectionFromSonarRule(sonarRule, SonarUtil.SonarRule.DescriptionSection.Key.how_to_fix).getContent();
        String root_cause = getDescriptionSectionFromSonarRule(sonarRule, SonarUtil.SonarRule.DescriptionSection.Key.root_cause).getContent();


        return Issue
                .builder()
                .runId(runId)
                .source(Issue.Source.SONAR)
                .metadata(Issue.SonarMetadata
                        .builder()
                        .description(
                                String.format("%s: %n%s%n%n%s: %n%s", "Introduction", introduction, "Root Cause", root_cause))
                        .suggestion(how_to_fix)
                        .filePath(filePath)
                        .codeSnippet(codeSnippet)
                        .issueKey(sonarIssue.getKey())
                        .ruleKey(sonarRule.getKey())
                        .build()
                )
                .title(sonarIssue.getMessage())
                .reliabilitySeverity(getSeverityFromSonarIssue(sonarIssue, SonarUtil.Impact.SoftwareQuality.RELIABILITY))
                .securitySeverity(getSeverityFromSonarIssue(sonarIssue, SonarUtil.Impact.SoftwareQuality.SECURITY))
                .maintainabilitySeverity(getSeverityFromSonarIssue(sonarIssue, SonarUtil.Impact.SoftwareQuality.MAINTAINABILITY))
                .effortInMinutes(parseEffortToMinutes(sonarIssue.getEffort()))
                .status(Issue.Status.SELECTED)
                .build();
    }

    private Issue.Severity getSeverityFromSonarIssue(SonarUtil.SonarIssue sonarIssue, SonarUtil.Impact.SoftwareQuality softwareQuality) {
        return sonarIssue.getImpacts().stream()
                .filter(i -> i.getSoftwareQuality() == softwareQuality)
                .findFirst()
                .map(SonarUtil.Impact::getSeverity)
                .orElse(null);
    }

    private SonarUtil.SonarRule.DescriptionSection getDescriptionSectionFromSonarRule(SonarUtil.SonarRule sonarRule, SonarUtil.SonarRule.DescriptionSection.Key key) {
        return sonarRule.getDescriptionSections().stream()
                .filter(ds -> ds.getKey() == key)
                .findFirst()
                .orElse(new SonarUtil.SonarRule.DescriptionSection(key, ""));
    }

    private String getFilePathFromSonarIssue(SonarUtil.SonarIssue sonarIssue) {
        return sonarIssue.getComponent().substring(sonarIssue.getProject().length() + 1);
    }

    private String getCodeSnippetFromSonarIssue(SonarUtil.SonarIssue sonarIssue, String containerName) {
        Integer startLine = sonarIssue.getTextRange().getStartLine();
        Integer endLine = sonarIssue.getTextRange().getEndLine();
        String filePath = getFilePathFromSonarIssue(sonarIssue);
        return processUtil.run("docker", "exec", containerName,
                "sed", "-n", startLine + "," + endLine + "p", "/workspace/repo/" + filePath);
    }

    private int parseEffortToMinutes(String effort) {
        if (effort == null) return 0;
        Map<String, Integer> map = Map.of("d", 480, "h", 60, "min", 1); // 1d = 8h = 480m
        Matcher m = Pattern.compile("(\\d+)\\s*(d|h|min)").matcher(effort);
        int total = 0;
        while (m.find()) {
            total += Integer.parseInt(m.group(1)) * map.get(m.group(2));
        }
        return total;
    }

    private String buildAiScanPrompt() {
        String typeRange = Arrays.stream(Issue.AiMetadata.Type.values())
                .map(t -> "\t\t- " + t.name() + ": " + t.getDescription())
                .collect(Collectors.joining("\n"));
        String severityRange = Arrays.stream(Issue.Severity.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        return """
                你是一个代码审查专家。请探索 /workspace/repo 下的代码，识别静态分析工具难以发现的代码质量问题。
                
                输出规则：
                - 只报告确认存在的真实问题。如果没有发现任何问题，返回空数组。
                - 只输出以下格式数组，不要任何额外文字或说明，不要用 Markdown 代码块，直接输出 JSON 数组本身。
                    ```json
                    [
                        {
                            "title": "请重构此方法，将其认知复杂度降低",
                            "filePath": "src/main/java/Foo.java",
                            "codeSnippet": "    return list(map(lambda item: MigrationScript(path=MIGRATION_DIR / item['name'], version=item['version'], description=item['description']), cursor.fetchall())))",
                            "type": "GOD_CLASS",
                            "effortInMinutes": "30",
                            "description": "...",
                            "suggestion": "...",
                            "reliabilitySeverity": "HIGH",
                            "securitySeverity": null,
                            "maintainabilitySeverity": "MEDIUM"
                        },
                        ...
                    ]
                    ```
                    - title:
                        - 字段含义: 一句话介绍要求开发人员做什么来解决此问题，这个字段值必须是中文
                    - filePath:
                        - 字段含义: 文件的相对路径
                    - codeSnippet:
                        - 字段含义: 相关代码片段（能帮助理解上下文即可）
                    - type:
                        - 字段含义: 问题类型
                        - 取值范围:
                %s
                    - effortInMinutes:
                        - 字段含义: 人工修复这个问题需要花费的分钟数
                    - description:
                        - 字段含义: 问题描述，说明问题什么时候会产生影响，以及问题的根本原因
                    - suggestion:
                        - 字段定义: 该类问题通用的修复方式，不得提供仅针对这一问题的特殊解法
                    - reliabilitySeverity:
                        - 字段含义: 逻辑缺陷严重程度
                        - 取值范围: %s, null
                    - securitySeverity:
                        - 字段含义: 安全漏洞严重程度
                        - 取值范围: %s, null
                    - maintainabilitySeverity:
                        - 字段含义: 代码异味严重程度
                        - 取值范围: %s, null
                - JSON 字符串值内部的所有双引号必须转义，否则 JSON 解析失败。
                """.formatted(typeRange, severityRange, severityRange, severityRange);
    }

    private String purifyLLMOutput(String rawOutput) {
        int start = rawOutput.indexOf('[');
        int end = rawOutput.lastIndexOf(']');
        if (start == -1 || end == -1 || start >= end) {
            return "[]";
        }
        return rawOutput.substring(start, end + 1).trim();
    }
}
