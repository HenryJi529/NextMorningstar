package com.morningstar.kill.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.kill.pojo.bo.UserDailyModeStats;
import com.morningstar.kill.pojo.po.Record;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RecordMapper extends BaseMapper<Record> {
    int insertIntoRecordAndUserRecord(@Param("record") Record record);

    List<UserDailyModeStats> selectDailyModeStats(@Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
