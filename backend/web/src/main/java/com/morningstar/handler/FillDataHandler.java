package com.morningstar.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
@Slf4j
public class FillDataHandler implements MetaObjectHandler {
    /**
     * 定义自动填充的方法
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Timestamp.class, new Timestamp(new Date().getTime()));
        this.strictInsertFill(metaObject, "updateTime", Timestamp.class, new Timestamp(new Date().getTime()));
    }

    /**
     * 定义更新时填充的方法
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", Timestamp.class, new Timestamp(new Date().getTime()));
    }
}
