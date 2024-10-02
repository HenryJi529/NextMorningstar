package com.morningstar.config;

import com.morningstar.properties.AsyncProperties;
import com.morningstar.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;


@Configuration
@EnableAsync
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AsyncConfig implements AsyncConfigurer {

    private final AsyncProperties asyncProperties;

    private final EmailUtil emailUtil;

    @Bean
    public RejectedExecutionHandler rejectedExecutionHandler() {
        return (r, executor) -> {
            log.warn("异步任务被拒绝: 当前阻塞队列任务数-{}, 当前活动线程数-{}, 线程池完成任务数-{}, 线程池总任务数-{}",
                    executor.getQueue().size(), executor.getActiveCount(), executor.getCompletedTaskCount(), executor.getTaskCount());
            emailUtil.sendSimpleEmailToAdmin("异步任务被拒绝", String.format("异步任务出现异常，当前线程池状态：%s", executor));
        };
    }

    @Override
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池创建时候初始化的线程数
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        // 允许核心线程超时关闭
        executor.setAllowCoreThreadTimeOut(true);
        // 只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        // 用来缓冲执行任务的队列
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        // 线程存活时间
        executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
        // 线程名称前缀
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        // 设置拒绝策略
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setRejectedExecutionHandler(rejectedExecutionHandler());
        executor.initialize();
        return executor;
    }
}
