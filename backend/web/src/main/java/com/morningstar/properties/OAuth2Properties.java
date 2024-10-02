package com.morningstar.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.oauth2")
@Data
public class OAuth2Properties {
    private String githubClientId;
    private String githubClientSecret;
    private String googleClientId;
    private String googleClientSecret;
}

