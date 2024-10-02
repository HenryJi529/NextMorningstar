package com.morningstar.practice.core;

import org.junit.jupiter.api.Test;

public class ThreadLocalTest {

    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    @Test
    public void test() {
        // 线程1
        new Thread(() -> {
            threadLocal.set("User1-Data");
            System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get()); // 输出 User1-Data
        }, "Thread-1").start();

        // 线程2
        new Thread(() -> {
            threadLocal.set("User2-Data");
            System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get()); // 输出 User2-Data
        }, "Thread-2").start();
    }
}
