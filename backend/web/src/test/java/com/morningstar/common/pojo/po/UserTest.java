package com.morningstar.common.pojo.po;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UserTest {
    @Test
    public void test() {
        User user = User.builder().username("小明").build();
        ObjectMapper objectMapper = JsonUtil.objectMapper();
        log.info(user.toString());
        try {
            // 将User对象转换为JSON字符串
            String jsonString = objectMapper.writeValueAsString(user);
            log.info(jsonString);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
    }
}
