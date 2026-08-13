package com.morningstar.dev.pojo.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.morningstar.dev.pojo.po.Issue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiIssue {
    private String title;
    private String filePath;
    private String codeSnippet;
    private Integer startLine;
    private Integer endLine;
    private Issue.AiMetadata.Type type;
    private Integer effortInMinutes;
    private String description;
    private String suggestion;
    private Issue.Severity reliabilitySeverity;
    private Issue.Severity securitySeverity;
    private Issue.Severity maintainabilitySeverity;
}
