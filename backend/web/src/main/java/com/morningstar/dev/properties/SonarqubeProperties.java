package com.morningstar.dev.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.app.dev.sonarqube")
@Data
public class SonarqubeProperties {
    private String token;
    private String backendOrigin;
    private String containerOrigin;
}
