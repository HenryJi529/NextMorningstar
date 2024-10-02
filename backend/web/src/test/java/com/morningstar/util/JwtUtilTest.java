package com.morningstar.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void test() {
        String token;
        token = jwtUtil.create(UUID.randomUUID(), "刘备", "主管");
        log.info(token);
        assert jwtUtil.parse(token) != null;
        log.info(Objects.requireNonNull(jwtUtil.parse(token)).toString());
        assert Objects.requireNonNull(jwtUtil.parse(token)).get(jwtUtil.USERNAME_CLAIM).equals("刘备");
        assert jwtUtil.getUsername(token).equals("刘备");
        assert jwtUtil.getRole(token).equals("主管");

        // 增删改都会验证错误
        assert jwtUtil.parse(token.substring(0, token.length() - 2)) == null;
        assert jwtUtil.parse(token.substring(0, 5) + "a" + token.substring(6)) == null;
        assert jwtUtil.parse(token + "123") == null;

        token = jwtUtil.create(UUID.randomUUID(), "曹操", "主管", 1000L);
        log.info(token);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 超时了
        assert jwtUtil.parse(token) == null;
    }
}
