package com.morningstar.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "morningstar.qrcode")
@Data
public class QrcodeProperties {
    private Integer size;
    private Integer margin;
    private String foreColor;
    private String backColor;
    private String errorCorrectionLevel;
    private Integer logoRatio;
}