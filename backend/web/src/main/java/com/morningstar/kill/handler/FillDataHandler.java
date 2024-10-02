package com.morningstar.kill.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
public class FillDataHandler implements MetaObjectHandler {

    /**
     * 定义自动填充的方法
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        //设置insert操作时的时间点
        metaObject.setValue("createTime", new Timestamp(new Date().getTime()));
        //设置update操作时的时间点
        metaObject.setValue("updateTime", new Timestamp(new Date().getTime()));
    }

    /**
     * 定义更新时填充的方法
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        //设置update操作时的时间点
        metaObject.setValue("updateTime", new Timestamp(new Date().getTime()));
    }
}
