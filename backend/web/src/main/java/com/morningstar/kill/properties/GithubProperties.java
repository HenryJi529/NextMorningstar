package com.morningstar.kill.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kill.github")
@Data
public class GithubProperties {
    private String token;
    private String username;
    private String repository;
}
 