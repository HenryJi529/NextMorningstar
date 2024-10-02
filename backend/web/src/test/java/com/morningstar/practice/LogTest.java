package com.morningstar.practice;

import com.morningstar.practice.service.PracticeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LogTest {
    @Autowired
    private PracticeService practiceService;

    @Test
    public void test() {
        practiceService.doSomething("内容");
    }
}
