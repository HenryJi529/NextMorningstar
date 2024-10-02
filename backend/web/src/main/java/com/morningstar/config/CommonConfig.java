package com.morningstar.config;

import cn.hutool.core.lang.Snowflake;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.util.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
    /**
     * 配置雪花算法Id生成器
     */
    @Bean
    public Snowflake snowflake() {
        return new Snowflake(1L, 1L);
    }

    /**
     * 配置Spring-web与Spring-websocket中都使用的ObjectMapper
     */
    @Bean
    ObjectMapper objectMapper() {
        return JsonUtil.objectMapper();
    }
}
