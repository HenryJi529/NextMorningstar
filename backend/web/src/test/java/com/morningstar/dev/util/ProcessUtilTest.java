package com.morningstar.dev.util;

import com.morningstar.dev.util.ProcessUtil.ProcessExecutionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessUtilTest {
    private final ProcessUtil processUtil = new ProcessUtil();

    @Test
    void testRunStripsTrailingNewline() {
        String output = processUtil.run("echo", "hello");
        assertEquals("hello", output);
    }

    @Test
    void testRunMultiLineStdout() {
        String output = processUtil.run("sh", "-c", "echo foo; echo bar");
        assertEquals("foo\nbar", output);
    }

    @Test
    void testRunPreservesOtherWhitespace() {
        // 行首空格、末尾空格都原样保留,只去末尾换行
        String output = processUtil.run("printf", "  spaced  \n");
        assertEquals("  spaced  ", output);
    }

    @Test
    void testNonZeroExitThrowsWithStderr() {
        ProcessExecutionException e = assertThrows(ProcessExecutionException.class,
                () -> processUtil.run("sh", "-c", "echo oops >&2; exit 3"));
        assertTrue(e.getMessage().contains("exit 3"), "异常信息应含退出码: " + e.getMessage());
        assertTrue(e.getMessage().contains("oops"), "异常信息应含 stderr: " + e.getMessage());
    }

    @Test
    void testLargeStderrNoDeadlock() {
        // stderr 超过管道缓冲区(64KB)时,若不在独立线程读会死锁
        ProcessExecutionException e = assertThrows(ProcessExecutionException.class,
                () -> processUtil.run("sh", "-c", "yes X | head -c 200000 >&2; exit 1"));
        assertTrue(e.getMessage().contains("exit 1"));
        // 200KB 的 stderr 应被完整接进异常消息(>64KB 即证明未被管道截断)
        assertTrue(e.getMessage().length() > 200_000,
                "stderr 应完整捕获(~200KB),实际消息长度: " + e.getMessage().length());
    }

    @Test
    void testCommandNotFoundThrows() {
        ProcessExecutionException e = assertThrows(ProcessExecutionException.class,
                () -> processUtil.run("definitely-not-a-real-command-xyz"));
        assertTrue(e.getMessage().contains("definitely-not-a-real-command-xyz"));
    }
}
