package com.morningstar.practice.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPoolTest {

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @BeforeEach
    public void contextLoad() {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.initialize();
    }

    public void printThreadPoolState() {
        System.out.println("当前线程数：" + threadPoolTaskExecutor.getThreadPoolExecutor().getPoolSize());
        System.out.println("当前压入队列的任务数:" + threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size());
        System.out.println("正在处理任务的线程数：" + threadPoolTaskExecutor.getThreadPoolExecutor().getActiveCount());
        System.out.println("完成任务数：" + threadPoolTaskExecutor.getThreadPoolExecutor().getCompletedTaskCount());
        System.out.println("总任务数：" + threadPoolTaskExecutor.getThreadPoolExecutor().getTaskCount());
    }

    @Test
    public void test1() throws InterruptedException {

        printThreadPoolState();

        log.info("并发3个任务");
        for (int i = 0; i < 3; i++) {
            threadPoolTaskExecutor.execute(new Task());
        }

        // 主线程休眠2秒中，保证其它3个线程处于空闲状态
        TimeUnit.SECONDS.sleep(2);

        printThreadPoolState();

        log.info("并发2个任务");
        for (int i = 0; i < 2; i++) {
            threadPoolTaskExecutor.execute(new Task());
        }
        Thread.sleep(500);

        printThreadPoolState();

    }

    @Slf4j
    public static class Task implements Runnable {
        @Override
        public void run() {
            log.info("开启任务...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("结束任务...");
        }
    }
}