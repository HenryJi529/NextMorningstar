package com.morningstar.practice.core;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

public class PathTest {
    @Test
    public void testResource() throws IOException {
        System.out.println(this.getClass().getResource("/excel/easyexcel_template1.xlsx"));
        System.out.println(this.getClass().getClassLoader().getResource("excel/easyexcel_template1.xlsx"));
        byte[] content = Objects.requireNonNull(this.getClass().getResourceAsStream("/static/logo.png")).readAllBytes();
        assert Math.round(content.length / 1024.0) == 19;
    }

    @Test
    public void testAppPath() {
        System.out.println(System.getProperty("java.class.path"));
        System.out.println(System.getProperty("user.dir"));
        System.out.println(this.getClass().getResource("/"));
    }
}
