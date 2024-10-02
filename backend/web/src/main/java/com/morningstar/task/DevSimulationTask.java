package com.morningstar.task;

import com.morningstar.kill.dao.mapper.RecordMapper;
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.kill.pojo.po.Record;
import com.morningstar.common.pojo.po.User;
import com.morningstar.util.FakerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

@Component
@Profile("dev")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DevSimulationTask {
    private final Random random = new Random();
    
    private final UserMapper userMapper;

    private final RecordMapper recordMapper;

    private final FakerUtil fakerUtil;

    @Scheduled(cron = "0 */5 * * * *")
    public void run() {
        int simulationInterval = 5;

        // 插入新用户
        int N = random.nextInt(2);
        System.out.println(ansi().eraseScreen().fg(GREEN).a(String.format("添加新用户x%d...", N)).reset());
        for(int i = 0; i < N; i++) {
            User fakerUser = fakerUtil.fakeUser(simulationInterval, TimeUnit.MINUTES);
            userMapper.insert(fakerUser);
        }

        // 插入新纪录
        int M = random.nextInt(4);
        System.out.println(ansi().eraseScreen().fg(GREEN).a(String.format("添加新纪录x%d...", M)).reset());
        long leftMostRecordCreated = Date.from(LocalDateTime.now().minusMinutes(simulationInterval).atZone(ZoneId.systemDefault()).toInstant()).getTime();
        for(int i = 0; i < M; i++) {
            Record fakerRecord = fakerUtil.fakeKillRecord(leftMostRecordCreated);
            recordMapper.insertIntoRecordAndUserRecord(fakerRecord);
        }
    }
}
