package com.morningstar.kill.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Slf4j
public class TestPasswordEncoder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testEncode(){
        String password = "1234asdw";
        log.info(passwordEncoder.encode(password));
    }

    @Test
    public void testMatches() {
        String password = "123456";
        assert passwordEncoder.matches(password, passwordEncoder.encode(password));
    }
}
