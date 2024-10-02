package com.morningstar.practice;

import org.junit.jupiter.api.Test;

public class AppPathTest {
    @Test
    public void test() {
        System.out.println(System.getProperty("java.class.path"));
        System.out.println(System.getProperty("user.dir"));
        System.out.println(this.getClass().getResource("/"));
    }
}
