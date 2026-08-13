package com.morningstar.dev.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.morningstar.dev.pojo.bo.RepoIdentity;
import com.morningstar.dev.properties.GiteaProperties;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class GiteaUtil {
    private static final Pattern REPO_PATTERN = Pattern.compile(
            "(?:https?://[^/]+/|git@[^:]+:)([^/]+)/([^/]+?)(?:\\.git)?/?$");
    private final RestTemplate restTemplate;
    private final GiteaProperties giteaProperties;

    public RepoIdentity parseRepoIdentity(String link) {
        Matcher matcher = REPO_PATTERN.matcher(link.trim());
        if (!matcher.matches()) {
            throw new BaseException(ResponseCode.DEV_PROJECT_REPO_LINK_INVALID, link);
        }
        return RepoIdentity.builder().ownerName(matcher.group(1)).repoName(matcher.group(2)).build();
    }

    public String getCodeSnippetLink(String filePath, Integer startLine, Integer endLine, String projectLink, String commitSha) {
        String fileLink = getFileLink(filePath, projectLink, commitSha);
        return String.format("%s#L%d-L%d", fileLink, startLine, endLine);
    }

    public String getFileLink(String filePath, String projectLink, String commitSha) {
        RepoIdentity repoIdentity = parseRepoIdentity(projectLink);
        return String.format("%s/%s/%s/src/commit/%s/%s", giteaProperties.getBackendOrigin(), repoIdentity.getOwnerName(), repoIdentity.getRepoName(), commitSha, filePath);
    }

    public String getCommitLink(String projectLink, String commitSha) {
        RepoIdentity repoIdentity = parseRepoIdentity(projectLink);
        return String.format("%s/%s/%s/commit/%s", giteaProperties.getBackendOrigin(), repoIdentity.getOwnerName(), repoIdentity.getRepoName(), commitSha);
    }

    public String formatRepoLink(String rawLink) {
        RepoIdentity repoIdentity = parseRepoIdentity(rawLink);
        return String.format("%s/%s/%s", giteaProperties.getBackendOrigin(), repoIdentity.getOwnerName(), repoIdentity.getRepoName());
    }

    private String getCollaboratorUrl(String ownerName, String repoName) {
        return String.format("%s/api/v1/repos/%s/%s/collaborators/%s",
                giteaProperties.getBackendOrigin(), ownerName, repoName, giteaProperties.getBotUsername());
    }

    private HttpHeaders getAdminAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "token " + giteaProperties.getAdminToken());
        return headers;
    }

    private HttpHeaders getBotAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "token " + giteaProperties.getBotToken());
        return headers;
    }

    public boolean isCollaborator(String link) {
        RepoIdentity id = parseRepoIdentity(link);
        try {
            restTemplate.exchange(
                    getCollaboratorUrl(id.getOwnerName(), id.getRepoName()),
                    HttpMethod.GET,
                    new HttpEntity<>(getAdminAuthHeaders()),
                    Void.class
            );
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }

    public void addCollaborator(String link) {
        RepoIdentity id = parseRepoIdentity(link);
        try {
            restTemplate.exchange(
                    getCollaboratorUrl(id.getOwnerName(), id.getRepoName()),
                    HttpMethod.PUT,
                    new HttpEntity<>(Map.of("permission", "write"), getAdminAuthHeaders()),
                    Void.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new BaseException(ResponseCode.DEV_PROJECT_REPO_NOT_FOUND,
                    id.getOwnerName() + "/" + id.getRepoName());
        }
    }

    public void removeCollaborator(String link) {
        RepoIdentity id = parseRepoIdentity(link);
        try {
            restTemplate.exchange(getCollaboratorUrl(id.getOwnerName(), id.getRepoName()), HttpMethod.DELETE, new HttpEntity<>(getAdminAuthHeaders()), Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BaseException(ResponseCode.DEV_PROJECT_REPO_NOT_FOUND,
                    id.getOwnerName() + "/" + id.getRepoName());
        }
    }

    public PullRequest createPullRequest(String link,
                                         String head, String base,
                                         String title, String body) {
        RepoIdentity id = parseRepoIdentity(link);
        return restTemplate.postForObject(
                String.format("%s/api/v1/repos/%s/%s/pulls",
                        giteaProperties.getBackendOrigin(), id.getOwnerName(),
                        id.getRepoName()),
                new HttpEntity<>(Map.of(
                        "head", head, "base", base, "title", title, "body", body),
                        getBotAuthHeaders()),
                PullRequest.class
        );
    }

    public PullRequest getPullRequest(String link, Integer prId) {
        RepoIdentity id = parseRepoIdentity(link);
        try {
            return restTemplate.exchange(
                    String.format("%s/api/v1/repos/%s/%s/pulls/%d",
                            giteaProperties.getBackendOrigin(), id.getOwnerName(), id.getRepoName(), prId),
                    HttpMethod.GET,
                    new HttpEntity<>(getBotAuthHeaders()),
                    PullRequest.class
            ).getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PullRequest {
        private Integer number;
        @JsonProperty("html_url")
        private String htmlUrl;
        private Boolean merged;
        private State state;

        public enum State {
            OPEN("open"),
            CLOSED("closed");

            private final String value;

            State(String value) {
                this.value = value;
            }

            @JsonCreator
            public static State fromValue(String value) {
                for (State s : values()) {
                    if (s.value.equals(value)) {
                        return s;
                    }
                }
                return null;
            }

            @JsonValue
            public String getValue() {
                return value;
            }
        }
    }
}
