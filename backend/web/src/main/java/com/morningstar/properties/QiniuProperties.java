package com.morningstar.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "morningstar.qiniu")
@Data
public class QiniuProperties {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String domain;
}