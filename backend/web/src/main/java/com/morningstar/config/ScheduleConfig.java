package com.morningstar.config;

import com.morningstar.properties.ScheduleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduleConfig implements SchedulingConfigurer {

    private final ScheduleProperties scheduleProperties;

    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(scheduleProperties.getPoolSize());
        scheduler.setThreadNamePrefix(scheduleProperties.getThreadNamePrefix());
        scheduler.initialize();
        taskRegistrar.setTaskScheduler(scheduler);
    }
}
