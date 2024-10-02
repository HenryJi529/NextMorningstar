package com.morningstar;

import com.morningstar.initializer.CommonInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.TimeZone;


@Slf4j
@SpringBootApplication
//@EnableTransactionManagement // Spring Boot 已自动配置事务
public class BackendApp {
    public static void main(String[] args) {
        // 打印默认时区
        System.out.printf("Default TimeZone: %s%n", TimeZone.getDefault());
        // 打印环境变量
        System.out.println("======= System Environment Variables =======");
        System.getenv().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.printf("%-40s = %s%n",
                                entry.getKey(),
                                entry.getValue()));
        SpringApplication.run(BackendApp.class, args);
    }

    @Bean
    CommandLineRunner init(CommonInitializer initializer) {
        return args -> {
            initializer.initialize(); // 初始化
        };
    }
}