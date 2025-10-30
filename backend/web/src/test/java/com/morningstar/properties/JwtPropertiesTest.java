package com.morningstar.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtPropertiesTest {
    @Autowired
    private JwtProperties jwtProperties;

    @Test
    public void test() {
        System.out.println(jwtProperties);
    }
}
