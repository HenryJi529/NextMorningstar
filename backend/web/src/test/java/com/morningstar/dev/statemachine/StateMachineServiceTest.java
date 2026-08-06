package com.morningstar.dev.statemachine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.pojo.vo.CreateProjectRequestVo;
import com.morningstar.dev.service.ProjectService;
import com.morningstar.dev.service.RunService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StateMachineServiceTest {
    private final StateMachineService stateMachineService;
    private final ProjectService projectService;
    private final RunService runService;
    private final ProjectMapper projectMapper;
    private Run run;

    @BeforeEach
    void setUp() {
        String repoLink = "http://127.0.0.1:7001/SpiderMan/backend";
        Project project = projectMapper.selectOne(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getLink, repoLink));
        if (project == null) {
            project = projectService.createProject(
                    CreateProjectRequestVo
                            .builder()
                            .name("test-project")
                            .link(repoLink)
                            .branchName("main")
                            .description("test-description")
                            .maxFixesPerRun(10)
                            .adminId(UUID.randomUUID())
                            .build()
            );
        }
        this.run = runService.createRun(project.getId());
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
        Thread.sleep(10000);
        stateMachineService.requestCancel(run.getId());

        Thread.sleep(100000);
    }
}
