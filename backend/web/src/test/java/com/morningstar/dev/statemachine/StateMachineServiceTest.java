package com.morningstar.dev.statemachine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.pojo.vo.CreateProjectRequestVo;
import com.morningstar.dev.service.ProjectService;
import com.morningstar.dev.service.RunService;
import com.morningstar.infra.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
class StateMachineServiceTest {
    private final StateMachineService stateMachineService;
    private final ProjectService projectService;
    private final RunService runService;
    private final ProjectMapper projectMapper;
    private final Integer timeout = 300;
    private Run run;

//    @BeforeEach
//    void setUp() {
//        String repoLink = "http://127.0.0.1:7001/SpiderMan/smart-union-hub";
//        Project project = projectMapper.selectOne(
//                new LambdaQueryWrapper<Project>()
//                        .eq(Project::getLink, repoLink));
//        if (project == null) {
//            project = projectService.createProject(
//                    CreateProjectRequestVo
//                            .builder()
//                            .name("智慧工会")
//                            .link(repoLink)
//                            .branchName("master")
//                            .maxAiIssuesPerRun(1)
//                            .maxSonarIssuesPerRun(2)
//                            .description("test-description")
//                            .adminId(UUID.randomUUID())
//                            .build()
//            );
//        }
//        this.run = runService.createRun(project.getId(), Run.TriggerType.SCHEDULED);
//        log.info("当前run: {}", run.getId());
//    }

//    @BeforeEach
//    void setUp() {
//        String repoLink = "http://127.0.0.1:7001/SpiderMan/AMLDashboard";
//        Project project = projectMapper.selectOne(
//                new LambdaQueryWrapper<Project>()
//                        .eq(Project::getLink, repoLink));
//        if (project == null) {
//            project = projectService.createProject(
//                    CreateProjectRequestVo
//                            .builder()
//                            .name("反洗钱驾驶舱")
//                            .link(repoLink)
//                            .branchName("main")
//                            .description("这是个反洗钱驾驶舱")
//                            .adminId(UUID.randomUUID())
//                            .build()
//            );
//        }
//        this.run = runService.createRun(project.getId(), Run.TriggerType.MANUAL);
//        log.info("当前run: {}", run.getId());
//    }

//    @BeforeEach
//    void setUp() {
//        String repoLink = "http://127.0.0.1:7001/SpiderMan/gaas-frontend";
//        Project project = projectMapper.selectOne(
//                new LambdaQueryWrapper<Project>()
//                        .eq(Project::getLink, repoLink));
//        if (project == null) {
//            project = projectService.createProject(
//                    CreateProjectRequestVo
//                            .builder()
//                            .name("SGS前端")
//                            .link(repoLink)
//                            .branchName("main")
//                            .description(RandomUtil.getChineseString(100))
//                            .adminId(UUID.randomUUID())
//                            .build()
//            );
//        }
//        this.run = runService.createRun(project.getId(), Run.TriggerType.MANUAL);
//        log.info("当前run: {}", run.getId());
//    }

    //    @BeforeEach
//    void setUp() {
//        String repoLink = "http://127.0.0.1:7001/SpiderMan/gaas-backend";
//        Project project = projectMapper.selectOne(
//                new LambdaQueryWrapper<Project>()
//                        .eq(Project::getLink, repoLink));
//        if (project == null) {
//            project = projectService.createProject(
//                    CreateProjectRequestVo
//                            .builder()
//                            .name("SGS后端")
//                            .link(repoLink)
//                            .branchName("main")
//                            .description(RandomUtil.getChineseString(100))
//                            .adminId(UUID.randomUUID())
//                            .build()
//            );
//        }
//        this.run = runService.createRun(project.getId(), Run.TriggerType.MANUAL);
//        log.info("当前run: {}", run.getId());
//    }

    @BeforeEach
    void setUp() {
        String repoLink = "http://127.0.0.1:7001/SpiderMan/OldSpringBackend";
        Project project = projectMapper.selectOne(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getLink, repoLink));
        if (project == null) {
            project = projectService.createProject(
                    CreateProjectRequestVo
                            .builder()
                            .name("Java8后端")
                            .link(repoLink)
                            .branchName("main")
                            .description(RandomUtil.getChineseString(100))
                            .adminId(UUID.randomUUID())
                            .build()
            );
        }
        this.run = runService.createRun(project.getId(), Run.TriggerType.MANUAL);
        log.info("当前run: {}", run.getId());
    }

    @Test
    @SuppressWarnings({"squid:S2699", "java:S2925"})
    void testNormal() throws InterruptedException {
        stateMachineService.sendEvent(run.getId(), Event.START);
        Thread.sleep((long) timeout * 60 * 1000);
    }

    @Test
    @SuppressWarnings({"squid:S2699", "java:S2925"})
    void testCancel() throws InterruptedException {
        stateMachineService.sendEvent(run.getId(), Event.START);
        Thread.sleep(10000);
        stateMachineService.requestCancel(run.getId());

        Thread.sleep((long) timeout * 60 * 1000);
    }
}
