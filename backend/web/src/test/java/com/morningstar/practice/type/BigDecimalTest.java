package com.morningstar.practice.type;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class BigDecimalTest {
    @Test
    public void test() {
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.2");

        assert !a.add(b).equals(new BigDecimal("0.30")); // 严格比较数值和标度
        assert a.add(b).compareTo(new BigDecimal("0.30")) == 0; // 只比较数值
    }

}
