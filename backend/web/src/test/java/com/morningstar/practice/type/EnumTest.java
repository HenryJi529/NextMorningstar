package com.morningstar.practice.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.morningstar.util.JsonUtil;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

public class EnumTest {
    @Test
    public void test1() {
        for (SimpleEnum e : EnumSet.allOf(SimpleEnum.class)) {
            System.out.println(e.name());
            System.out.println(e.ordinal());
            System.out.println(e);
            System.out.println("==========");
        }
    }

    @Test
    public void test2() {
        System.out.println(SimpleEnum.valueOf("INSERT"));
    }

    @Test
    public void test3() throws JsonProcessingException {
        System.out.println(JsonUtil.objectMapper().readValue("\"INSERT\"", SimpleEnum.class));
        System.out.println(JsonUtil.objectMapper().writeValueAsString(SimpleEnum.INSERT));
    }

    enum SimpleEnum {
        INSERT,
        UPDATE,
    }
}
