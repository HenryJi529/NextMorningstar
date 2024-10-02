package com.morningstar.kill.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.morningstar.kill.properties.GithubProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class GithubUtil {
    @Autowired
    private GithubProperties githubProperties;

    @Autowired
    private RestTemplate restTemplate;

    public HttpHeaders headers = new HttpHeaders();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.setAccept(Collections.singletonList(MediaType.valueOf("application/vnd.github+json")));
        headers.set("Authorization", "Bearer " + githubProperties.getToken());
        headers.set("X-GitHub-Api-Version", "2022-11-28");
    }

    public JsonNode parseJson(String body){
        try {
            return objectMapper.readTree(body);
        } catch (IllegalArgumentException ignored){
            return null;
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String formatJson(Object params){
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode getRepo() {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = String.format("https://api.github.com/repos/%s/%s", githubProperties.getUsername(), githubProperties.getRepository());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode listPublicRepos() {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = String.format("https://api.github.com/users/%s/repos", githubProperties.getUsername());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode createPublicRepo(String repoName) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", repoName);
        params.put("private", false);
        HttpEntity<String> entity = new HttpEntity<>(formatJson(params), headers);
        ResponseEntity<String> response = restTemplate.exchange("https://api.github.com/user/repos", HttpMethod.POST, entity, String.class);

        return parseJson(response.getBody());
    }

    public JsonNode deleteRepo(String repoName) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = String.format("https://api.github.com/repos/%s/%s", githubProperties.getUsername(), repoName);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

        return parseJson(response.getBody());
    }
}
