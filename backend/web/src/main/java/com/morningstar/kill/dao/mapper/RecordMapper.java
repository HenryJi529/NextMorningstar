package com.morningstar.kill.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.kill.pojo.bo.UserDailyModeStats;
import com.morningstar.kill.pojo.po.Record;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
* @description 针对表【record】的数据库操作Mapper
* @Entity com.morningstar.pojo.entity.Record
*/
public interface RecordMapper extends BaseMapper<Record> {
    int insertIntoRecordAndUserRecord(@Param("record") Record record);

    List<UserDailyModeStats> selectRecentDailyModeStats(@Param("n") int n, @Param("userId") UUID userId);
}
