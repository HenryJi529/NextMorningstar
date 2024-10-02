package com.morningstar.practice;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SpringBootTest
@Configuration
public class BeanTest {
    @Test
    public void test() {
        System.out.println("1. 创建Spring应用上下文");
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("2. 从容器中获取Bean实例");
        NiceBean niceBean = context.getBean(NiceBean.class);

        System.out.println("3. 使用niceBean");
        System.out.println(niceBean);

        System.out.println("4. 关闭Spring应用上下文");
        context.close();

        System.out.println("5. 实验结束");

    }
}

class NiceBean {
    public void init() {
        System.out.println("init()");
    }

    public NiceBean() {
        System.out.println("NiceBean()");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("postConstruct()...");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("preDestroy()...");
    }

    public void close() {
        System.out.println("close()");
    }
}

class AppConfig {
    @Bean(initMethod = "init", destroyMethod = "close")
    NiceBean niceBean() {
        return new NiceBean();
    }
}