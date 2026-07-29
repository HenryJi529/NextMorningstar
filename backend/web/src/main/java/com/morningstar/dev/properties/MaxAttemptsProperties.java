package com.morningstar.dev.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.app.dev.max-attempts")
@Data
public class MaxAttemptsProperties {
    private Integer sync;
    private Integer scan;
    private Integer fix;
    private Integer verify;
    private Integer submit;
}
