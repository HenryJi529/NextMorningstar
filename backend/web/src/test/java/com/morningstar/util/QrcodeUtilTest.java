package com.morningstar.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QrcodeUtilTest {
    @Autowired
    private QrcodeUtil qrcodeUtil;

    @Test
    public void testGenerateAsBase64() {
        System.out.println(qrcodeUtil.generateAsBase64("https://central.sonatype.com/"));
    }
}
