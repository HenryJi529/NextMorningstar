package com.morningstar.task;

import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.pojo.po.User;
import com.morningstar.kill.dao.mapper.RecordMapper;
import com.morningstar.kill.pojo.po.Record;
import com.morningstar.util.FakerUtil;
import com.morningstar.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DevSimulationTask {
    private final Random random = new Random();

    private final UserMapper userMapper;

    private final RecordMapper recordMapper;

    private final FakerUtil fakerUtil;

    @Value("${morningstar.simulation.interval}")
    private Integer simulationInterval;

    @Scheduled(cron = "0 */${morningstar.simulation.interval} * * * *")
    public void run() {
        log.info("模拟日常运行......");

        // 插入新用户
        int N = random.nextInt(2);
        System.out.println(ansi().fg(GREEN).a(String.format("添加新用户x%d...", N)).reset());
        for (int i = 0; i < N; i++) {
            User fakerUser = fakerUtil.fakeUser(simulationInterval, TimeUnit.MINUTES);
            userMapper.insert(fakerUser);
        }

        // 插入新纪录
        int M = random.nextInt(4);
        System.out.println(ansi().fg(GREEN).a(String.format("添加新纪录x%d...", M)).reset());
        for (int i = 0; i < M; i++) {
            long earliestCreateMilliseconds = TimeUtil.convertLocalDateTimeToDate(TimeUtil.getCurrentLocalDateTime().minusMinutes(simulationInterval)).getTime();
            Record fakerRecord = fakerUtil.fakeKillRecord(earliestCreateMilliseconds);
            recordMapper.insertIntoRecordAndUserRecord(fakerRecord);
        }
    }
}
