package com.morningstar.lab.config;

import com.morningstar.lab.web.interceptor.LabInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LabWebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new LabInterceptor())
                .addPathPatterns("/lab/**")
                .excludePathPatterns("/lab/parse-excel");
    }
}
