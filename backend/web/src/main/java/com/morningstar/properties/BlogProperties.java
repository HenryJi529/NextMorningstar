package com.morningstar.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.blog")
@Data
public class BlogProperties {
    private Integer excerptLength;
    private String description;
    private String rootPath;
    private String rssTitle;
    private String atomTitle;
}
