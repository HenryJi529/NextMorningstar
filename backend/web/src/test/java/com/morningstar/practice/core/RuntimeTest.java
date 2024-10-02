package com.morningstar.practice.core;

import com.morningstar.util.MathUtil;
import org.junit.jupiter.api.Test;

public class RuntimeTest {
    @Test
    public void testHardware() {
        System.out.println("available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("total memory: " + MathUtil.round((double)Runtime.getRuntime().totalMemory()/1024/1024, 2) + "M");
        System.out.println("free memory: " + MathUtil.round((double)Runtime.getRuntime().freeMemory()/1024/1024, 2) + "M");
        System.out.println("max memory: " + MathUtil.round((double)Runtime.getRuntime().maxMemory()/1024/1024, 2) + "M");
    }
}
