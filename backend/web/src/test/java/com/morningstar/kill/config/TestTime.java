package com.morningstar.kill.config;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class TestTime {
    @Test
    public void test() {
        LocalDateTime localDateTime = LocalDateTime.now();
        // 转换为 ZonedDateTime，使用系统默认时区
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        // 转换为 Date
        Date date = Date.from(zonedDateTime.toInstant());
        System.out.println(date.getTime());
    }
}
