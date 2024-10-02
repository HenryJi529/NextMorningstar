package com.morningstar.kill.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EchoTask {
    @Scheduled(cron = "0 */5 * * * *")
    public void echo() {
        System.out.println("ECHO TASK");
    }
}
