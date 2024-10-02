package com.morningstar.kill.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestQiniuUtil {
    @Autowired
    private QiniuUtil qiniuUtil;

    @Test
    public void testGetUploadToken(){
        System.out.println(qiniuUtil.getUploadToken());
    }

    @Test
    public void testGetDownloadUrl(){
        System.out.println(qiniuUtil.getDownloadUrl("mweb/16527488175792.jpg"));
    }
}
