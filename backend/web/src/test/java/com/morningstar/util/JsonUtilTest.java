package com.morningstar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JsonUtilTest {
    @Test
    public void testFormat() throws JsonProcessingException {
        System.out.println(JsonUtil.objectMapper().writeValueAsString(LocalDate.now()));
        System.out.println(JsonUtil.objectMapper().writeValueAsString(LocalDateTime.now()));
    }
}
