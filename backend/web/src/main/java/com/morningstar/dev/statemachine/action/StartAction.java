package com.morningstar.dev.statemachine.action;

import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.properties.ClaudeCodeProperties;
import com.morningstar.dev.properties.SandboxProperties;
import com.morningstar.dev.properties.SonarqubeProperties;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.StartResult;
import com.morningstar.dev.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class StartAction extends AbstractAction {
    private final ProcessUtil processUtil;
    private final SandboxProperties sandboxProperties;
    private final ClaudeCodeProperties claudeCodeProperties;
    private final SonarqubeProperties sonarqubeProperties;
    private final RunMapper runMapper;

    public StartAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper,
                       ProcessUtil processUtil,
                       SandboxProperties sandboxProperties,
                       ClaudeCodeProperties claudeCodeProperties,
                       SonarqubeProperties sonarqubeProperties,
                       RunMapper runMapper) {
        super(stateMachineService, actionAttemptMapper, Event.START_SUCCEEDED, Event.START_FAILED);
        this.processUtil = processUtil;
        this.sandboxProperties = sandboxProperties;
        this.claudeCodeProperties = claudeCodeProperties;
        this.sonarqubeProperties = sonarqubeProperties;
        this.runMapper = runMapper;
    }

    @Override
    public Action.Type getType() {
        return Action.Type.START;
    }

    @Override
    protected StartResult doExecute(UUID runId) {
        UUID projectId = runMapper.selectById(runId).getProjectId();
        String volumeName = sandboxProperties.getVolumeNamePrefix() + projectId;
        String containerName = sandboxProperties.getContainerNamePrefix() + runId;

        try {
            processUtil.run("docker", "volume", "create", volumeName);
            processUtil.run(
                    "docker", "run", "-d",
                    "--name", containerName,
                    "-v", volumeName + ":/workspace/repo",
                    "-e", "MODEL_API_KEY=" + claudeCodeProperties.getModelApiKey(),
                    "-e", "SONARQUBE_TOKEN=" + sonarqubeProperties.getToken(),
                    "--add-host", "host.docker.internal:host-gateway",
                    sandboxProperties.getImage());
            return StartResult
                    .builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .volumeName(volumeName)
                    .containerName(containerName)
                    .build();
        } catch (ProcessUtil.ProcessExecutionException e) {
            return StartResult.builder()
                    .status(ActionResult.Status.FAILED)
                    .message(e.getMessage())
                    .build();
        }
    }
}
