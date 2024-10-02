package com.morningstar.practice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

@Slf4j
public class Md5Test {
    @Test
    public void test() {
        String md5 = DigestUtils.md5DigestAsHex("123456".getBytes());
        log.info(md5);
    }
}
