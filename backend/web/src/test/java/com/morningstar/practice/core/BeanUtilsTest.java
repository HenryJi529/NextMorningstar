package com.morningstar.practice.core;

import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;


public class BeanUtilsTest {
    @Test
    public void testCopyProperties() {
        Human source = Human.builder().name("小白").build();
        Man target = Man.builder().cock(20.1).age(18).build();
        BeanUtils.copyProperties(source, target);
        System.out.println(source);
        System.out.println(target);
        assert target.age == null;
    }

    @Data
    @Builder
    static class Human {
        private String name;
        private Integer age;
    }

    @Data
    @Builder
    static class Man {
        private String name;
        private Integer age;
        private Double cock;
    }

}
