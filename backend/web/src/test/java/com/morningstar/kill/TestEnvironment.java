package com.morningstar.kill;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestEnvironment {
    @Autowired
    private Environment environment;

    @Test
    public void test() {
        System.out.println(environment.getProperty("spring.application.name"));
    }
}
