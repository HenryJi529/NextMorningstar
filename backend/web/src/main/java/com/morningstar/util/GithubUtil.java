package com.morningstar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.exception.BaseException;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GithubUtil {
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setAccept(Collections.singletonList(MediaType.valueOf("application/vnd.github+json")));
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    private JsonNode parseJson(String body) {
        if (body == null) {
            return null;
        }
        try {
            return objectMapper.readTree(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatJson(Object params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode getUser(String token) {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders(token));
        ResponseEntity<String> response = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode getUser(String token, String username) {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders(token));
        String url = String.format("https://api.github.com/users/%s", username);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return parseJson(response.getBody());
    }

    public GithubAccount getUserAsAccount(String token) {
        JsonNode userJsonNode = getUser(token);
        return GithubAccount
                .builder()
                .id(userJsonNode.get("id").asText())
                .username(userJsonNode.get("login").asText())
                .email(userJsonNode.get("email").asText())
                .avatarUrl(userJsonNode.get("avatar_url").asText())
                .build();
    }

    public JsonNode listRepos(String token) {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders(token));
        ResponseEntity<String> response = restTemplate.exchange("https://api.github.com/user/repos", HttpMethod.GET, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode getRepo(String token, String repoName) {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders(token));
        String url = String.format("https://api.github.com/repos/%s/%s", getUserAsAccount(token).getUsername(), repoName);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode createPrivateRepo(String token, String repoName) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", repoName);
        params.put("private", true);
        HttpEntity<String> entity = new HttpEntity<>(formatJson(params), getHeaders(token));
        ResponseEntity<String> response = restTemplate.exchange("https://api.github.com/user/repos", HttpMethod.POST, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode createPublicRepo(String token, String repoName) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", repoName);
        params.put("private", false);
        HttpEntity<String> entity = new HttpEntity<>(formatJson(params), getHeaders(token));
        ResponseEntity<String> response = restTemplate.exchange("https://api.github.com/user/repos", HttpMethod.POST, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode deleteRepo(String token, String repoName) {
        HttpEntity<String> entity = new HttpEntity<>(getHeaders(token));
        String url = String.format("https://api.github.com/repos/%s/%s", getUserAsAccount(token).getUsername(), repoName);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode createFile(String token, String repoName, String path, byte[] bytesContent) {
        String base64Content = Base64.getEncoder().encodeToString(bytesContent);
        return createFile(token, repoName, path, base64Content);
    }

    public JsonNode createFile(String token, String repoName, String path, String base64Content) {
        long base64Size = base64Content.getBytes(StandardCharsets.US_ASCII).length;
        if (base64Size >= Math.ceil((100.0 * 1024 * 1024) / 3) * 4) {
            throw new BaseException("Github不支持超过100M的文件上传(请使用LFS)");
        }
        GithubAccount githubAccount = getUserAsAccount(token);
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", githubAccount.getUsername(), repoName, path);

        Map<String, Object> params = new HashMap<>();
        params.put("message", String.format("create file [%s]", path));
        Map<String, String> committer = new HashMap<>();
        committer.put("name", githubAccount.getUsername());
        committer.put("email", githubAccount.getEmail());
        params.put("committer", committer);
        params.put("content", base64Content);

        HttpEntity<String> entity = new HttpEntity<>(formatJson(params), getHeaders(token));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode getFileOrDir(String token, String repoName, String path) {
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", getUserAsAccount(token).getUsername(), repoName, path);

        HttpEntity<String> entity = new HttpEntity<>(getHeaders(token));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode deleteFile(String token, String repoName, String path) {
        GithubAccount githubAccount = getUserAsAccount(token);
        String sha;
        try {
            sha = getFileOrDir(token, repoName, path).get("sha").asText();
        } catch (NullPointerException e) {
            throw new BaseException(String.format("Git路径[%s]不是文件", path));
        }
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", githubAccount.getUsername(), repoName, path);

        Map<String, Object> params = new HashMap<>();
        params.put("message", String.format("delete file [%s]", path));
        Map<String, String> committer = new HashMap<>();
        committer.put("name", githubAccount.getUsername());
        committer.put("email", githubAccount.getUsername());
        params.put("committer", committer);
        params.put("sha", sha);

        HttpEntity<String> entity = new HttpEntity<>(formatJson(params), getHeaders(token));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        return parseJson(response.getBody());
    }

    @Data
    @Builder
    public static class GithubAccount {
        private String id;
        private String username;
        private String email;
        private String avatarUrl;
    }
}
