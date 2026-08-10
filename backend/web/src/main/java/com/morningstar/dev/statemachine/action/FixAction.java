package com.morningstar.dev.statemachine.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.dev.dao.mapper.ActionAttemptMapper;
import com.morningstar.dev.dao.mapper.IssueMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.properties.SandboxProperties;
import com.morningstar.dev.statemachine.AbstractAction;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.StateMachineService;
import com.morningstar.dev.statemachine.result.ActionResult;
import com.morningstar.dev.statemachine.result.FixResult;
import com.morningstar.dev.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class FixAction extends AbstractAction {
    private final SandboxProperties sandboxProperties;
    private final RunMapper runMapper;
    private final IssueMapper issueMapper;
    private final ProcessUtil processUtil;
    private final ObjectMapper objectMapper;

    public FixAction(StateMachineService stateMachineService, ActionAttemptMapper actionAttemptMapper, SandboxProperties sandboxProperties, RunMapper runMapper, IssueMapper issueMapper, ProcessUtil processUtil, ObjectMapper objectMapper) {
        super(stateMachineService, actionAttemptMapper, Event.FIX_SUCCEEDED, Event.FIX_FAILED);
        this.sandboxProperties = sandboxProperties;
        this.runMapper = runMapper;
        this.issueMapper = issueMapper;
        this.processUtil = processUtil;
        this.objectMapper = objectMapper;
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

        int fixedSonarIssueNum = 0;
        int fixedAiIssueNum = 0;

        try {
            return FixResult
                    .builder()
                    .status(ActionResult.Status.SUCCEEDED)
                    .fixedSonarIssueNum(fixedSonarIssueNum)
                    .fixedAiIssueNum(fixedAiIssueNum)
                    .build();
        } catch (ProcessUtil.ProcessExecutionException e) {
            return FixResult
                    .builder()
                    .status(ActionResult.Status.FAILED)
                    .message(e.getMessage())
                    .fixedSonarIssueNum(fixedSonarIssueNum)
                    .fixedAiIssueNum(fixedAiIssueNum)
                    .build();
        }
    }
}
