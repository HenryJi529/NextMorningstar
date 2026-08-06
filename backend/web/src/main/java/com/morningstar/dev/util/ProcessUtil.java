package com.morningstar.dev.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 命令行进程执行工具(ProcessBuilder):执行 + 捕获 stdout + 非零退出抛 {@link ProcessExecutionException}(带 stderr)。
 */
@Component
@Slf4j
public class ProcessUtil {
    /**
     * 执行命令,返回 stdout(仅去掉末尾换行符,其余空白原样保留);非零退出抛异常。
     * stderr 单独起线程读,避免缓冲区填满导致死锁。
     */
    public String run(String... command) {
        List<String> commandList = List.of(command);
        log.info("执行命令: {}", String.join(" ", commandList));
        try {
            Process process = new ProcessBuilder(commandList).start();
            ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
            Thread stderrReader = new Thread(() -> {
                try {
                    process.getErrorStream().transferTo(stderrBuffer);
                } catch (IOException ignored) {
                    // 进程提前退出导致流关闭,内容以 waitFor 后已读到的为准
                }
            });
            stderrReader.start();

            String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            stderrReader.join();

            String stderr = stderrBuffer.toString(StandardCharsets.UTF_8);
            if (exitCode != 0) {
                throw new ProcessExecutionException(commandList, exitCode, stderr);
            }
            return stdout.replaceAll("\\R+$", "");
        } catch (IOException e) {
            throw new ProcessExecutionException(commandList, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProcessExecutionException(commandList, e);
        }
    }

    /**
     * 判断这个命令是否成功
     */
    public boolean test(String... args) {
        try {
            run(args);
            return true;
        } catch (ProcessExecutionException e) {
            return false;
        }
    }

    public static class ProcessExecutionException extends RuntimeException {
        public ProcessExecutionException(List<String> command, int exitCode, String stderr) {
            super("命令执行失败(exit " + exitCode + "): " + String.join(" ", command)
                    + (stderr.isBlank() ? "" : "\n" + stderr.trim()));
        }

        public ProcessExecutionException(List<String> command, Throwable cause) {
            super("命令执行异常: " + String.join(" ", command), cause);
        }
    }
}
