package com.morningstar.config;

import com.morningstar.properties.AsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncConfig implements AsyncConfigurer {

    private final AsyncProperties asyncProperties;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池创建时候初始化的线程数
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        // 只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        // 用来缓冲执行任务的队列
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        // 线程名称前缀
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
