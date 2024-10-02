package com.morningstar.practice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
}
