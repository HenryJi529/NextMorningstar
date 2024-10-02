package com.morningstar.practice.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Slf4j
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void test() {
        String password = "1234asdw";
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("Bcrypt密码: " + encodedPassword);
        assert passwordEncoder.matches(password, encodedPassword);
    }
}
