package com.morningstar.practice.core;

import org.junit.jupiter.api.Test;

public class WrapperClassTest {

    @Test
    public void testCache() {
        Integer a1 = 1, a2 = 1, b1 = 128, b2 = 128;
        assert a1 == a2;
        assert b1 != b2;
    }
}
