package com.morningstar.dev.statemachine.action;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.bo.AiIssue;
import com.morningstar.dev.pojo.bo.StructuredAiScanOutput;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.properties.InScopeSeverities;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.ScanResult;
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
    private final SonarUtil sonarUtil;
    private final IssueMapper issueMapper;
    private final InScopeSeverities inScopeSeverities;
    private final CommonSteps commonSteps;

    public ScanAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, ProcessUtil processUtil, RunMapper runMapper, ProjectMapper projectMapper, SonarUtil sonarUtil, IssueMapper issueMapper, InScopeSeverities inScopeSeverities, CommonSteps commonSteps) {
        super(stateMachineService, actionAttemptMapper, Event.SCAN_SUCCEEDED, Event.SCAN_FAILED);
        this.processUtil = processUtil;
        this.runMapper = runMapper;
        this.projectMapper = projectMapper;
        this.sonarUtil = sonarUtil;
        this.issueMapper = issueMapper;
        this.inScopeSeverities = inScopeSeverities;
        this.commonSteps = commonSteps;
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
        String containerName = commonSteps.getContainerName(run);

        try {
            // 清除旧 issue
            issueMapper.delete(new LambdaQueryWrapper<Issue>().eq(Issue::getRunId, runId));

            // Maven 构建
            commonSteps.mavenBuild(run);

            // Sonar 扫描
            List<SonarUtil.SonarIssue> sonarIssues = commonSteps.sonarScan(run, project);
            if (!sonarIssues.isEmpty()) {
                log.info("Sonar Issue典型对象: {}", sonarIssues.get(0));
            }

            // AI 扫描
            List<AiIssue> aiIssues = commonSteps.runClaude(
                    CommonSteps.ClaudeInput
                            .builder()
                            .intent(CommonSteps.ClaudeInput.Intent.SCAN)
                            .prompt(buildAiScanPrompt())
                            .build(),
                    run,
                    StructuredAiScanOutput.class
            ).getIssues().stream().filter(aiIssue -> {
                List<Issue.Severity> scope = inScopeSeverities.getAi();
                return scope.contains(aiIssue.getMaintainabilitySeverity()) || scope.contains(aiIssue.getSecuritySeverity()) || scope.contains(aiIssue.getReliabilitySeverity());
            }).toList();

            if (!aiIssues.isEmpty()) {
                log.info("AI Issue典型对象: {}", aiIssues.get(0));
            }

            // 选择 Sonar 和 AI 生成的 Issue, 转换并落表
            List<Issue> issues = new ArrayList<>();

            List<SonarUtil.SonarIssue> selectedSonarIssues = new ArrayList<>(sonarIssues);
            Collections.shuffle(selectedSonarIssues);
            if (selectedSonarIssues.size() > project.getMaxSonarIssuesPerRun()) {
                selectedSonarIssues = selectedSonarIssues.subList(0, project.getMaxSonarIssuesPerRun());
            }

            issues.addAll(selectedSonarIssues.stream().map(sonarIssue -> convertSonarIssueToIssue(sonarIssue, containerName, runId)).toList());
            List<AiIssue> selectedAiIssues = new ArrayList<>(aiIssues);
            Collections.shuffle(selectedAiIssues);
            if (aiIssues.size() > project.getMaxAiIssuesPerRun()) {
                selectedAiIssues = selectedAiIssues.subList(0, project.getMaxAiIssuesPerRun());
            }
            issues.addAll(selectedAiIssues.stream().map(aiIssue -> convertAiIssueToIssue(aiIssue, runId)).toList());

            issueMapper.insert(issues);

            return ScanResult
                    .builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .scannedSonarIssueNum(sonarIssues.size())
                    .scannedAiIssueNum(aiIssues.size())
                    .scannedSonarIssueKeys(sonarIssues.stream().map(SonarUtil.SonarIssue::getKey).toList())
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
                        .startLine(aiIssue.getStartLine())
                        .endLine(aiIssue.getEndLine())
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
                        .startLine(sonarIssue.getTextRange().getStartLine())
                        .endLine(sonarIssue.getTextRange().getEndLine())
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
                .map(t -> "\t\t- %s: %s(%s)".formatted(t.name(), t.getName(), t.getDescription()))
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
                            "startLine": 10,
                            "endLine": 20,
                            "type": "GOD_CLASS",
                            "effortInMinutes": 30,
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
                    - startLine:
                        - 字段含义: 代码片段的起始行号
                        - 取值要求: 必须用工具读取该文件，根据实际行号填写，禁止凭记忆估算
                    - endLine:
                        - 字段含义: 代码片段的结束行号
                        - 取值要求: 必须用工具读取该文件，根据实际行号填写，禁止凭记忆估算
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
}
