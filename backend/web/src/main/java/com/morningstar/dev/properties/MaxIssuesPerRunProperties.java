package com.morningstar.dev.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.app.dev.max-issues-per-run")
@Data
public class MaxIssuesPerRunProperties {
    private Integer sonar;
    private Integer ai;
}
