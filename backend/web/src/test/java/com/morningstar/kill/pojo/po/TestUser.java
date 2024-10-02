package com.morningstar.kill.pojo.po;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TestUser {
    @Test
    public void test() {
        User user = User.builder().username("小明").build();
        ObjectMapper objectMapper = new ObjectMapper();
        log.info(user.toString());
        try {
            // 将User对象转换为JSON字符串
            String jsonString = objectMapper.writeValueAsString(user);
            log.info(jsonString);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
