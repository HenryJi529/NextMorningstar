package com.morningstar.dev.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.app.dev.git")
@Data
public class GitProperties {
    private String fixBranchPrefix;
}
