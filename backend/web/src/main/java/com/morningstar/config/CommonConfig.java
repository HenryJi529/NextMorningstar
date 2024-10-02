package com.morningstar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.util.IdWorker;
import com.morningstar.util.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
    /**
     * 配置id生成器bean
     */
    @Bean
    public IdWorker idWorker() {
        //基于运维人员对机房和机器的编号规划自行约定
        return new IdWorker(1L, 2L);
    }

    /**
     * 配置Spring-web与Spring-websocket中都使用的ObjectMapper
     */
    @Bean
    ObjectMapper objectMapper() {
        return JsonUtil.objectMapper();
    }
}
