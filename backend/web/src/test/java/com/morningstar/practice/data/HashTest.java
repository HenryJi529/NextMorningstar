package com.morningstar.practice.data;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import java.util.zip.CRC32;

@Slf4j
public class HashTest {
    @Test
    public void testCRC32(){
        CRC32 crc1 = new CRC32();
        crc1.update("hello1212121212 ".getBytes());
        crc1.update("world3434343434".getBytes());

        CRC32 crc2 = new CRC32();
        crc2.update("hello1212121212 world3434343434".getBytes());

        assert crc1.getValue() == crc2.getValue();
    }


    @Test
    public void testMD5() {
        String md5 = DigestUtils.md5DigestAsHex("好大一头猪～～".getBytes());
        System.out.println("MD5结果: " + md5);
    }
}
