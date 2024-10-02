package com.morningstar.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


public class JsonUtil {
    public static ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();

        // (支持Java8的日期时间类型)
        mapper.findAndRegisterModules();

        // 设置Long的序列化
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(Long.TYPE, new ToStringSerializer());
        mapper.registerModule(module);

        return mapper;
    }
}
