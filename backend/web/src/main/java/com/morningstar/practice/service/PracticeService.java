package com.morningstar.practice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PracticeService {

    public String doSomething(String input) {
        log.debug("service: 这是一个DEBUG日志信息");
        log.info("service: 这是一个INFO日志信息");
        System.out.println("执行业务逻辑: " + input);
        return "处理结果: " + input.toUpperCase();
    }

    public void throwException() {
        throw new RuntimeException("测试异常");
    }

    @Async
    public void prolongedTask() {
        System.out.println(Thread.currentThread().getName());
        log.info("任务开始: {}", Thread.currentThread().getName());
        try {
            Thread.sleep(3000); // 模拟耗时3秒的任务
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("任务结束");
    }
}
