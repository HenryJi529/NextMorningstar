package com.morningstar.practice.data;

import cn.hutool.core.util.HexUtil;
import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class EncodeTest {
    @Test
    public void testBase64Encode() {
        byte[] bytesContent = new byte[73 * 1024 * 1024]; // 73 MB 的模拟数据
        long bytesSize = bytesContent.length;

        String base64Content = Base64.getEncoder().encodeToString(bytesContent);
        long base64Size = base64Content.getBytes(StandardCharsets.US_ASCII).length;

        assert base64Size == Math.ceil(bytesSize / 3.0) * 4;
    }

    @Test
    public void testBase64Decode() {
        String content = "好大一头猪～～";
        String base64Content = Base64.getEncoder().encodeToString(content.getBytes());
        assert content.equals(new String(Base64.getDecoder().decode(base64Content)));
    }

    @Test
    public void testBase65UrlEncode() {
        String content = "一點都不會累 我已經跳了三天三夜 我現在的心情喝汽水也會醉 oh 完全都不會疲倦 我還要再跳三天三夜 我現在的心情輕得好像可以飛 oh O.K.O.K 加入我的行列 No.k No.k. 白天跳到黑夜 Always Always 快樂不會吃虧 No way No way 誰也別想拒絕 三天三夜的三更半夜 跳舞不要停歇 三天三夜的三更半夜 飄浮只靠音樂";
        String base64ContentUrlSafe = Base64.getUrlEncoder().encodeToString(content.getBytes());
        String base64Content = base64ContentUrlSafe.replace('-', '+').replace('_', '/'); // + 表示 url中的空格

        assert content.equals(new String(Base64.getUrlDecoder().decode(base64ContentUrlSafe)));
        assert content.equals(new String(Base64.getDecoder().decode(base64Content)));
    }

    @Test
    public void testUrlEncode(){
        String content = "hello, /你是个大 大 大苹果/";
        System.out.println("编码结果: " + URLEncoder.encode(content, StandardCharsets.UTF_8));
        assert content.equals(URLDecoder.decode(content, StandardCharsets.UTF_8));
    }

    @Test
    public void testHexDecode(){
        String content = "F212";
        System.out.println(Arrays.toString(HexUtil.decodeHex(content)));
    }
}
