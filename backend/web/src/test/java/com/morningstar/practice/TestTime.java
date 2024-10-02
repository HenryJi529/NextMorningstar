package com.morningstar.practice;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Date;

public class TestTime {
    @Test
    public void test1() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);

        // LocalDateTime 转换为 Date
        ZonedDateTime zonedDateTime1 = localDateTime.atZone(ZoneId.systemDefault());
        System.out.println(zonedDateTime1);
        System.out.println(ZoneId.getAvailableZoneIds());
        Date date = Date.from(zonedDateTime1.toInstant());
        System.out.println(date.getTime());

        // Date 转换为 LocalDateTime
        System.out.println(new Date().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
    }

    @Test
    public void test2() {
        LocalDate localDate = LocalDate.of(2025,1,30);
        LocalDateTime localDateTime1 = LocalDateTime.of(localDate.plusDays(1), LocalTime.MIN);
        LocalDateTime localDateTime2 = LocalDateTime.of(localDate, LocalTime.MAX);
        assert localDateTime1.isAfter(localDateTime2);
    }
}
