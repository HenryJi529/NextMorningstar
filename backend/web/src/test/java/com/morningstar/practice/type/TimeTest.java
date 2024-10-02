package com.morningstar.practice.type;

import com.morningstar.util.TimeUtil;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class TimeTest {
    @Test
    public void testConvert() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);

        // 可用时区
        System.out.println(ZoneId.getAvailableZoneIds());

        // LocalDateTime 转换为 Date
        System.out.println(TimeUtil.convertLocalDateTimeToDate(localDateTime));

        // Date 转换为 LocalDateTime
        System.out.println(TimeUtil.convertDateToLocalDateTime(TimeUtil.convertLocalDateTimeToDate(localDateTime)));

        // LocalDateTime 转化为 ZonedDateTime
        System.out.println(localDateTime.atZone(ZoneId.systemDefault()));

        Instant instant = Instant.now();
        // Instant 转换为 ZonedDateTime
        System.out.println(instant.atZone(ZoneId.of("Asia/Shanghai")));
        System.out.println(instant.atZone(ZoneId.of("America/New_York")));

        // Instant 转换为 LocalDateTime
        System.out.println(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @Test
    public void testCompare() {
        LocalDate localDate = LocalDate.of(2025, 1, 30);
        LocalDateTime localDateTime1 = LocalDateTime.of(localDate.plusDays(1), LocalTime.MIN);
        LocalDateTime localDateTime2 = LocalDateTime.of(localDate, LocalTime.MAX);
        assert localDateTime1.isAfter(localDateTime2);

        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 12, 30);
        Duration duration = Duration.between(start, end);
        System.out.println("时间差总秒数: " + duration.getSeconds() + "; 转换小时数: " + duration.toHours() + "; 分钟数: " + duration.toMinutes());

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 3, 15);
        Period period = Period.between(startDate, endDate);
        System.out.println("间隔: " + period.getYears() + "年 " + period.getMonths() + "月 " + period.getDays() + "天");
    }

    @Test
    public void testFormat() {
        System.out.println(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDate.now()));
        System.out.println(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(ZonedDateTime.now(ZoneId.of("America/New_York")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssVV").format(ZonedDateTime.now(ZoneId.of("America/New_York"))));
    }

    @Test
    public void testParse() throws ParseException {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2025-07-23 15:30:45"));
        System.out.println(LocalDateTime.parse("2025-07-23T15:30:45"));
        System.out.println(ZonedDateTime.parse("2025-07-23T15:30:45-05:00[America/New_York]"));
        System.out.println(LocalDateTime.parse("2025-07-23 15:30:45", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println(ZonedDateTime.parse("2025-07-23 15:30:45-05:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX")));
    }

    @Test
    public void testSetTimeZone() {
        System.out.println("默认" + TimeZone.getDefault());

        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        System.out.println(TimeZone.getDefault());
        System.out.println(ZoneId.systemDefault());
        LocalDateTime newYorkTime = LocalDateTime.now();
        System.out.println(newYorkTime); // 显示纽约时区的当前时间(无时区标记)

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        System.out.println(TimeZone.getDefault());
        System.out.println(ZoneId.systemDefault());
        LocalDateTime shanghaiTime = LocalDateTime.now();
        System.out.println(shanghaiTime); // 显示上海时区的当前时间(无时区标记)
    }
}
