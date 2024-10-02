package com.morningstar;

import com.morningstar.initializer.CommonInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;


@Slf4j
@SpringBootApplication
//@EnableTransactionManagement // Spring Boot 已自动配置事务
public class BackendApp {
    public static void main(String[] args) {
        log.info("Default TimeZone: {}", TimeZone.getDefault());
        SpringApplication.run(BackendApp.class, args);
    }

    @Bean
    CommandLineRunner init(CommonInitializer initializer) {
        return args -> {
            initializer.initialize(); // 初始化
        };
    }
}