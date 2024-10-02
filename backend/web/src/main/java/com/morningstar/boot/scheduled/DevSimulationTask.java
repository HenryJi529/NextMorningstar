package com.morningstar.boot.scheduled;

import com.morningstar.system.dao.mapper.UserMapper;
import com.morningstar.system.pojo.po.User;
import com.morningstar.system.util.SystemFakeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class DevSimulationTask {
    private final Random random = new Random();

    private final UserMapper userMapper;

    private final SystemFakeUtil systemFakeUtil;

    @Value("${morningstar.simulation.interval}")
    private Integer simulationInterval;

    @Scheduled(cron = "0 */${morningstar.simulation.interval} * * * *")
    public void run() {
        log.info("模拟日常运行......");

        // 插入新用户
        int N = random.nextInt(2);
        System.out.println(ansi().fg(GREEN).a(String.format("添加新用户x%d...", N)).reset());
        for (int i = 0; i < N; i++) {
            User fakerUser = systemFakeUtil.fakeUser(simulationInterval, TimeUnit.MINUTES);
            userMapper.insert(fakerUser);
        }
    }
}
