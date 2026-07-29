package com.morningstar.dev.statemachine;

import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.infra.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class StateMachineServiceTest {
    private final StateMachineService stateMachineService;
    private final ProjectMapper projectMapper;
    private final RunMapper runMapper;
    private Run run;

    @BeforeEach
    void setUp() {
        Project project = Project
                .builder()
                .id(UUID.randomUUID())
                .link(RandomUtil.getEnglishString(12))
                .maxFixesPerRun(10)
                .build();
        projectMapper.insert(project);
        this.run = Run
                .builder()
                .id(UUID.randomUUID())
                .projectId(project.getId())
                .state(State.PENDING)
                .status(Run.Status.RUNNING)
                .build();
        runMapper.insert(this.run);
    }


    @Test
    @SuppressWarnings({"squid:S2699", "java:S2925"})
    void testNormal() throws InterruptedException {
        stateMachineService.sendEvent(run.getId(), Event.START);
        Thread.sleep(100000);
    }

    @Test
    @SuppressWarnings({"squid:S2699", "java:S2925"})
    void testCancel() throws InterruptedException {
        stateMachineService.sendEvent(run.getId(), Event.START);
        Thread.sleep(5000);
        stateMachineService.requestCancel(run.getId());

        Thread.sleep(100000);
    }
}
