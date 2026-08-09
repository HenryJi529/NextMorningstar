package com.morningstar.dev.util;

import com.morningstar.dev.pojo.bo.RepoIdentity;
import com.morningstar.dev.properties.GiteaProperties;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.ResponseCode;
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

    public String formatRepoLink(String rawLink) {
        RepoIdentity repoIdentity = parseRepoIdentity(rawLink);
        return String.format("%s/%s/%s", giteaProperties.getBackendOrigin(), repoIdentity.getOwnerName(), repoIdentity.getRepoName());
    }

    private String collaboratorUrl(String ownerName, String repoName) {
        return String.format("%s/api/v1/repos/%s/%s/collaborators/%s",
                giteaProperties.getBackendOrigin(), ownerName, repoName, giteaProperties.getBotUsername());
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "token " + giteaProperties.getAdminToken());
        return headers;
    }

    public boolean isCollaborator(String link) {
        RepoIdentity id = parseRepoIdentity(link);
        try {
            restTemplate.exchange(
                    collaboratorUrl(id.getOwnerName(), id.getRepoName()),
                    HttpMethod.GET,
                    new HttpEntity<>(authHeaders()),
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
                    collaboratorUrl(id.getOwnerName(), id.getRepoName()),
                    HttpMethod.PUT,
                    new HttpEntity<>(Map.of("permission", "write"), authHeaders()),
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
            restTemplate.exchange(collaboratorUrl(id.getOwnerName(), id.getRepoName()), HttpMethod.DELETE, new HttpEntity<>(authHeaders()), Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BaseException(ResponseCode.DEV_PROJECT_REPO_NOT_FOUND,
                    id.getOwnerName() + "/" + id.getRepoName());
        }

    }
}
