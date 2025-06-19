package com.morningstar.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class JsonUtil {
    private static SimpleModule getLongModule(){
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addSerializer(Long.TYPE, new ToStringSerializer());
        return module;
    }

    private static SimpleModule getLocalDateTimeModule(){
        SimpleModule module = new SimpleModule();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        return module;
    }

    public static ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY); // 不推荐，存在安全风险, @JsonIgnore失效

        // (支持Java8的日期时间类型)
        mapper.findAndRegisterModules();
        // 设置Long的序列化
        mapper.registerModule(getLongModule());
        // 设置LocalDateTime的序列化与反序列化
        mapper.registerModule(getLocalDateTimeModule());

        return mapper;
    }
}
