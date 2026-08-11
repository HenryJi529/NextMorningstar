package com.morningstar.dev.properties;

import com.morningstar.dev.pojo.po.Issue;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "morningstar.app.dev.in-scope-severities")
@Data
public class InScopeSeverities {
    private List<Issue.Severity> sonar;
    private List<Issue.Severity> ai;
}
