package com.morningstar.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QiniuUtilTest {
    @Autowired
    private QiniuUtil qiniuUtil;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testGetUploadToken() {
        System.out.println(qiniuUtil.getUploadToken());
    }

    @Test
    public void testGetDownloadUrl() {
        System.out.println(qiniuUtil.getDownloadUrl("mweb/16527488175792.jpg"));
    }

    @Test
    void testUploadFile() {
        byte[] content = Objects.requireNonNull(restTemplate.getForObject("https://picsum.photos/200/300", byte[].class));
        qiniuUtil.uploadFile("测试图片.jpg", content);
    }
}
