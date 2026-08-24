package com.morningstar.dev.statemachine.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.dev.pojo.bo.RepoIdentity;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.properties.InScopeSeverities;
import com.morningstar.dev.properties.SandboxProperties;
import com.morningstar.dev.properties.SonarqubeProperties;
import com.morningstar.dev.util.GiteaUtil;
import com.morningstar.dev.util.ProcessUtil;
import com.morningstar.dev.util.SonarUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonSteps {
    private final GiteaUtil giteaUtil;
    private final SandboxProperties sandboxProperties;
    private final ProcessUtil processUtil;
    private final SonarqubeProperties sonarqubeProperties;
    private final SonarUtil sonarUtil;
    private final InScopeSeverities inScopeSeverities;
    private final ObjectMapper objectMapper;

    public String getContainerName(Run run) {
        return sandboxProperties.getContainerNamePrefix() + run.getId();
    }

    public String getVolumeName(Run run) {
        return sandboxProperties.getVolumeNamePrefix() + run.getProjectId();
    }

    public String getVolumeName(Project project) {
        return sandboxProperties.getVolumeNamePrefix() + project.getId();
    }

    public String getSonarProjectKey(Project project) {
        RepoIdentity repoIdentity = giteaUtil.parseRepoIdentity(project.getLink());
        return repoIdentity.getOwnerName() + ":" + repoIdentity.getRepoName();
    }

    public String getHeadCommitSha(Run run) {
        return processUtil.run(
                "docker", "run", "--rm",
                "-v", getVolumeName(run) + ":/workspace/repo",
                "alpine/git",
                "-c", "safe.directory=/workspace/repo",
                "-C", "/workspace/repo",
                "rev-parse", "HEAD");
    }

    public void mavenBuild(Run run) {
        String containerName = getContainerName(run);
        processUtil.run(
                "docker", "exec", containerName, "bash", "-c",
                "p=$(find /workspace/repo -name pom.xml | head -1); if [ -n \"$p\" ]; then mvn -s /workspace/maven-settings.xml -q compile -f \"$p\"; fi"
        );
    }

    public List<SonarUtil.SonarIssue> sonarScan(Run run, Project project) {
        String containerName = getContainerName(run);
        String sonarProjectKey = getSonarProjectKey(project);
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

        String ceTaskId = processUtil.run(
                "docker", "exec", containerName, "bash", "-c",
                "sed -n 's/^ceTaskId=//p' /tmp/.scannerwork/report-task.txt").trim();
        sonarUtil.waitForCeTask(ceTaskId);

        return sonarUtil.getAllOpenSonarIssuesByProjectKey(sonarProjectKey)
                .stream()
                .filter(sonarIssue ->
                        sonarIssue.getImpacts()
                                .stream()
                                .anyMatch(impact -> inScopeSeverities.getSonar().contains(impact.getSeverity()))
                )
                .toList();
    }

    public <T> T runClaude(ClaudeInput claudeInput, Run run, Class<T> outputType) throws JsonProcessingException {
        String containerName = getContainerName(run);
        String prompt = claudeInput.getPrompt();
        String schema = loadSchema(claudeInput.getIntent());

        // 提示词写入文件
        String promptFilePath = "/tmp/" + claudeInput.getIntent() + "_PROMPT.txt";
        String script = "cat > " + promptFilePath + " << 'EOF_PROMPT'\n"
                + prompt + "\nEOF_PROMPT";
        processUtil.run("docker", "exec", containerName, "bash", "-c", script);

        // schema写入文件
        String schemaPath = "/tmp/" + claudeInput.getIntent() + "_SCHEMA.json";
        processUtil.run("docker", "exec", containerName, "bash", "-c",
                "cat > " + schemaPath + " << 'EOF_SCHEMA'\n" + schema +
                        "\nEOF_SCHEMA");

        // 执行
        String envelope = processUtil.run(
                "docker", "exec", "-w", "/workspace/repo", containerName,
                "bash", "-c",
                "claude --dangerously-skip-permissions --print \"$(cat " +
                        promptFilePath + ")\" "
                        + "--output-format json --json-schema \"$(cat " +
                        schemaPath + ")\"");
        log.info("[{}] 模型原始输出: {}", claudeInput.getIntent(), envelope);
        // 拆封
        JsonNode so = objectMapper.readTree(envelope).get("structured_output");
        if (so == null) {
            throw new IllegalStateException("structured_output 缺失,重试上限已耗尽");
        }
        return objectMapper.treeToValue(so, outputType);
    }

    private String loadSchema(ClaudeInput.Intent intent) {
        try {
            return new
                    ClassPathResource("schemas/dev_" + intent.name().toLowerCase() + ".json").getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("加载 schema 失败: " + intent, e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ClaudeInput {
        private String prompt;
        private Intent intent;

        public enum Intent {
            SCAN,
            FIX,
            VERIFY
        }
    }
}
