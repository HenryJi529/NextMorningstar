package com.morningstar.dev.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.app.dev.gitea")
@Data
public class GiteaProperties {
    private String origin;

    private String botUsername;

    private String botToken;

    private String adminToken;
}

