package com.morningstar.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PermPropertiesTest {
    @Autowired
    private PermProperties permProperties;

    @Test
    void test() {
        System.out.println(permProperties);
    }
}
