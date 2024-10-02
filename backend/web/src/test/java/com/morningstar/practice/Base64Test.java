package com.morningstar.practice;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Test {
    @Test
    public void test(){
        byte[] bytesContent = new byte[73 * 1024 * 1024]; // 73 MB 的模拟数据
        long bytesSize = bytesContent.length;

        String base64Content = Base64.getEncoder().encodeToString(bytesContent);
        long base64Size = base64Content.getBytes(StandardCharsets.US_ASCII).length;

        assert base64Size == Math.ceil(bytesSize / 3.0) * 4;
    }
}
