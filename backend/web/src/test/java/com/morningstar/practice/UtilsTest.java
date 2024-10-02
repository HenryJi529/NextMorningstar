package com.morningstar.practice;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

public class UtilsTest {
    @Test
    public void testRandomStringUtils(){
        System.out.println(RandomStringUtils.random(10, "abc"));
    }
}
