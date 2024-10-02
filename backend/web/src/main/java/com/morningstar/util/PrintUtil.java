package com.morningstar.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class PrintUtil {
    /**
     * 用于隐藏System.out输出的方法
     *
     * @param action 需要执行的代码块（匿名函数）
     */
    public static void suppressSystemOut(Runnable action) {
        // 保存原始的System.out
        PrintStream originalOut = System.out;

        // 创建一个ByteArrayOutputStream来捕获输出
        PrintStream ps = new PrintStream(new ByteArrayOutputStream());

        try {
            // 重定向System.out到ByteArrayOutputStream
            System.setOut(ps);

            // 执行传入的代码块
            action.run();
        } finally {
            // 恢复System.out为原始输出流
            System.setOut(originalOut);
        }
    }
}
