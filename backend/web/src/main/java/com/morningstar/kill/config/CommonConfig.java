package com.morningstar.kill.config;

import com.morningstar.kill.util.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
    /**
     * 配置id生成器bean
     */
    @Bean
    public IdWorker idWorker(){
        //基于运维人员对机房和机器的编号规划自行约定
        return new IdWorker(1L,2L);
    }
}
