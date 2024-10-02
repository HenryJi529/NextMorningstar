package com.morningstar.kill.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestInitProperties {
    @Autowired
    private InitProperties initProperties;

    @Test
    void test() {
        System.out.println(initProperties);
    }
}
