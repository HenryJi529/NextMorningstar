package com.morningstar.practice;

import org.junit.jupiter.api.Test;

public class PathTest {
    @Test
    public void testResource(){
        System.out.println(this.getClass().getResource("/excel/easyexcel_template1.xlsx"));
        System.out.println(this.getClass().getClassLoader().getResource("excel/easyexcel_template1.xlsx"));
    }
}
