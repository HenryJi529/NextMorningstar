package com.morningstar.dev.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.dev.properties.SonarqubeProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SonarUtil {
    private final RestClient restClient;
    private final SonarqubeProperties sonarqubeProperties;

    public List<SonarIssue> getAllOpenSonarIssuesByProjectKey(String projectKey) {
        SonarIssueResponse pageInfoResponse = restClient.get()
                .uri(sonarqubeProperties.getBackendOrigin() + "/api/issues/search?componentKeys={projectKey}&issueStatuses=OPEN&ps=1", projectKey)
                .headers(h -> h.setBasicAuth(sonarqubeProperties.getToken(), ""))
                .retrieve()
                .body(SonarIssueResponse.class);
        if (pageInfoResponse == null || pageInfoResponse.getPaging() == null) {
            return null;
        }
        List<SonarIssue> sonarIssues = new ArrayList<>();
        Integer totalSonarIssueNum = pageInfoResponse.getPaging().getTotal();
        for (int i = 1; i <= Math.ceil(totalSonarIssueNum / 500.0); i++) {
            SonarIssueResponse response = restClient.get()
                    .uri(sonarqubeProperties.getBackendOrigin() + "/api/issues/search?componentKeys={projectKey}&issueStatuses=OPEN&ps=500&p=" + i, projectKey)
                    .headers(h -> h.setBasicAuth(sonarqubeProperties.getToken(), ""))
                    .retrieve()
                    .body(SonarIssueResponse.class);

            if (response == null || response.getIssues() == null) {
                return null;
            }
            sonarIssues.addAll(response.getIssues());
        }
        return sonarIssues;
    }

    public SonarRule getSonarRuleByKey(String key) {
        SonarRuleResponse response = restClient.get()
                .uri(sonarqubeProperties.getBackendOrigin() + "/api/rules/show?key={key}", key)
                .headers(h -> h.setBasicAuth(sonarqubeProperties.getToken(), ""))
                .retrieve()
                .body(SonarRuleResponse.class);

        if (response == null) {
            return null;
        }
        return response.getRule();
    }

    public SonarIssue getSonarIssueByKey(String key) {
        SonarIssueResponse response = restClient.get()
                .uri(sonarqubeProperties.getBackendOrigin() + "/api/issues/search?issues={key}", key)
                .headers(h -> h.setBasicAuth(sonarqubeProperties.getToken(), ""))
                .retrieve()
                .body(SonarIssueResponse.class);

        if (response == null || response.getIssues() == null || response.getIssues().isEmpty()) {
            return null;
        }
        return response.getIssues().get(0);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SonarIssueResponse {
        private Paging paging;
        private List<SonarIssue> issues;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SonarRuleResponse {
        private SonarRule rule;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Paging {
        private Integer total;
        private Integer pageIndex;
        private Integer pageSize;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SonarRule {
        private String key;
        private String name;
        private List<Impact> impacts;
        private List<DescriptionSection> descriptionSections;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DescriptionSection {
            private Key key;
            private String content;

            public enum Key {
                introduction,
                resources,
                root_cause,
                how_to_fix
            }
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SonarIssue {
        private String key;
        private String rule;
        private Status issueStatus;
        private String component;
        private String project;
        private TextRange textRange;
        private String message;
        private String effort;
        private List<Impact> impacts;

        public enum Status {
            OPEN,
            CONFIRMED,
            ACCEPTED,
            FALSE_POSITIVE,
            FIXED
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TextRange {
            private Integer startLine;
            private Integer endLine;
            private Integer startOffset;
            private Integer endOffset;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Impact {
        private SoftwareQuality softwareQuality;
        private Issue.Severity severity;

        public enum SoftwareQuality {
            RELIABILITY, SECURITY, MAINTAINABILITY
        }
    }
}
