package com.morningstar.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.schedule")
@Data
public class ScheduleProperties {
    private Integer poolSize;
    private String threadNamePrefix;
}