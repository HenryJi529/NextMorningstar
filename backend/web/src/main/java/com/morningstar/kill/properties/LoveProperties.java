package com.morningstar.kill.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "kill.love")
@Data
public class LoveProperties {
    private List<String> photos;
}
