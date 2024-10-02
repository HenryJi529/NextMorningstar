package com.morningstar;

import com.morningstar.common.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EnvironmentTest {
    @Autowired
    private Environment environment;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void test() {
        System.out.println(environment.getProperty("spring.application.name"));
        System.out.println(webApplicationContext.getBean(UserService.class));
    }
}
