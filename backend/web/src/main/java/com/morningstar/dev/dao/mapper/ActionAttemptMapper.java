package com.morningstar.dev.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.dev.pojo.po.ActionAttempt;
import com.morningstar.dev.statemachine.Action;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

public interface ActionAttemptMapper extends BaseMapper<ActionAttempt> {
    Integer selectMaxAttemptNo(@Param("runId") UUID runId, @Param("actionType") Action.Type actionType);

    ActionAttempt selectLatestActionAttempt(@Param("runId") UUID runId, @Param("actionType") Action.Type actionType);
}
