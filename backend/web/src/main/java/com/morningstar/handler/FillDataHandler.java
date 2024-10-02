package com.morningstar.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.morningstar.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class FillDataHandler implements MetaObjectHandler {
    /**
     * 定义自动填充的方法
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, TimeUtil.getCurrentLocalDateTime());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, TimeUtil.getCurrentLocalDateTime());
    }

    /**
     * 定义更新时填充的方法
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, TimeUtil.getCurrentLocalDateTime());
    }
}
