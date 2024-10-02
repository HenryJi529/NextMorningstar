package com.morningstar.util;

public class MathUtil {
    public static double round(double value, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("小数位数不能为负数");
        }
        double factor = Math.pow(10, scale);
        return (double) Math.round(value * factor) / factor;
    }
}
