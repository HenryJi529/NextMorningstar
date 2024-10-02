package com.morningstar;

import com.morningstar.initializer.CommonDataInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
//@EnableTransactionManagement // Spring Boot 已自动配置事务
public class BackendApp {
    public static void main(String[] args) {
        SpringApplication.run(BackendApp.class, args);
    }

    @Bean
    CommandLineRunner init(CommonDataInitializer dataInitializer) {
        return args -> {
            dataInitializer.initializeData(); // 调用数据初始化
        };
    }
}