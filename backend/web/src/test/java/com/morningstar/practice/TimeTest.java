package com.morningstar.practice;

import com.morningstar.util.TimeUtil;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeTest {
    @Test
    public void test1() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);

        // 可用时区
        System.out.println(ZoneId.getAvailableZoneIds());

        // LocalDateTime 转换为 Date
        System.out.println(TimeUtil.convertLocalDateTimeToDate(localDateTime));


        // Date 转换为 LocalDateTime
        System.out.println(TimeUtil.convertDateToLocalDateTime(TimeUtil.convertLocalDateTimeToDate(localDateTime)));
    }

    @Test
    public void test2() {
        LocalDate localDate = LocalDate.of(2025,1,30);
        LocalDateTime localDateTime1 = LocalDateTime.of(localDate.plusDays(1), LocalTime.MIN);
        LocalDateTime localDateTime2 = LocalDateTime.of(localDate, LocalTime.MAX);
        assert localDateTime1.isAfter(localDateTime2);
    }

    @Test
    public void test3() {
        System.out.println(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDate.now()));
        System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
    }
}
