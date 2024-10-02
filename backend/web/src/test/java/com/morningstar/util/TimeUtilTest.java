package com.morningstar.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class TimeUtilTest {
    @Test
    public void testGetDayDiff(){
        LocalDate endDate = TimeUtil.getCurrentLocalDate();
        LocalDate beginDate = endDate.minusDays(7);
        assert TimeUtil.getDayDiff(beginDate, endDate) == 7;
    }
}
