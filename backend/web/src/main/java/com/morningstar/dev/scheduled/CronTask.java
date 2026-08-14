package com.morningstar.dev.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.dev.dao.mapper.ProjectMapper;
import com.morningstar.dev.dao.mapper.RunMapper;
import com.morningstar.dev.pojo.po.Project;
import com.morningstar.dev.pojo.po.Run;
import com.morningstar.dev.service.RunService;
import com.morningstar.dev.statemachine.Event;
import com.morningstar.dev.statemachine.State;
import com.morningstar.dev.statemachine.StateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component("devCronTask")
@RequiredArgsConstructor
@Slf4j
public class CronTask {
    private final ProjectMapper projectMapper;
    private final RunMapper runMapper;
    private final RunService runService;
    private final StateMachineService stateMachineService;

    @Value("${morningstar.app.dev.schedule.max-concurrency}")
    private int maxConcurrency;

    @Value("${morningstar.app.dev.schedule.run-timeout-minutes}")
    private int runTimeoutMinutes;

    /**
     * 夜间扫描：为每个已启用的项目创建一个任务
     */
    @Scheduled(cron = "${morningstar.app.dev.schedule.create-cron}")
    public void nightlyCreateRuns() {
        log.info("夜间扫描开始");
        List<Project> enabledProjects = projectMapper.selectList(
                new LambdaQueryWrapper<Project>().eq(Project::getEnabled, true)
        );
        log.info("共 {} 个已启用项目", enabledProjects.size());
        for (Project project : enabledProjects) {
            if (runService.hasActiveRun(project.getId())) {
                log.info("项目 {} 已有活跃 run，跳过", project.getId());
                continue;
            }
            runService.createRun(project.getId());
        }
    }

    /**
     * 分发待处理的任务
     */
    @Scheduled(cron = "${morningstar.app.dev.schedule.dispatch-cron}")
    public void dispatchPendingRuns() {
        // 查当前有多少 run 正在执行中（非 PENDING 且非终态）
        long activeCount = runMapper.selectCount(
                new LambdaQueryWrapper<Run>()
                        .notIn(Run::getState, Set.of(
                                State.PENDING, State.CLEANED, State.FAILED
                        ))
        );

        int availableSlots = maxConcurrency - (int) activeCount;
        if (availableSlots <= 0) return;

        // 捞出 PENDING 的 run，按创建时间排队
        List<Run> pendingRuns = runMapper.selectList(
                new LambdaQueryWrapper<Run>()
                        .eq(Run::getState, State.PENDING)
                        .orderByAsc(Run::getCreateTime)
                        .last("LIMIT " + availableSlots)
        );

        for (Run run : pendingRuns) {
            stateMachineService.sendEvent(run.getId(), Event.START);
        }
    }


    /**
     * 任务超时：取消长时间无响应的任务
     */
    @Scheduled(cron = "${morningstar.app.dev.schedule.timeout-cron}")
    public void cancelTimeoutRuns() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(runTimeoutMinutes);
        List<Run> stuckRuns = runMapper.selectList(
                new LambdaQueryWrapper<Run>()
                        .ge(Run::getCreateTime, LocalDateTime.now().minusHours(24))
                        .lt(Run::getUpdateTime, deadline)
                        .notIn(Run::getState, Set.of(State.PENDING, State.CLEANED))
        );

        for (Run run : stuckRuns) {
            log.warn("任务超时取消: runId={}, state={}", run.getId(), run.getState());
            stateMachineService.requestCancel(run.getId());
        }
    }

    /**
     * 次日早上清理：清掉所有非终态的任务
     */
    @Scheduled(cron = "${morningstar.app.dev.schedule.cleanup-cron}")
    public void cancelOvernightRuns() {
        List<Run> activeRuns = runMapper.selectList(
                new LambdaQueryWrapper<Run>()
                        .ge(Run::getCreateTime, LocalDateTime.now().minusHours(24))
                        .notIn(Run::getState, Set.of(
                                State.CLEANING, State.CLEANED, State.FAILED
                        ))
        );

        if (activeRuns.isEmpty()) return;

        log.info("早上清理：共 {} 个 run 待取消", activeRuns.size());
        for (Run run : activeRuns) {
            if (run.getState() == State.PENDING) {
                // PENDING 没启动过，直接删除，不绕过状态机
                runMapper.deleteById(run.getId());
            } else {
                stateMachineService.requestCancel(run.getId());
            }
        }
    }

    /**
     * 同步 PR 状态：轮询所有 OPEN 的 PR，回写合并/拒绝结果
     */
    @Scheduled(cron = "${morningstar.app.dev.schedule.sync-pr-status-cron}")
    public void syncPrStatus() {
        List<Run> openPrRuns = runMapper.selectList(
                new LambdaQueryWrapper<Run>()
                        .isNotNull(Run::getPrId)
                        .eq(Run::getPrStatus, Run.PrStatus.OPEN)
        );
        for (Run run : openPrRuns) {
            try {
                runService.syncPrStatus(run.getId());
            } catch (Exception e) {
                log.error("[{}] 同步 PR 状态失败", run.getId(), e);
            }
        }
    }
}
