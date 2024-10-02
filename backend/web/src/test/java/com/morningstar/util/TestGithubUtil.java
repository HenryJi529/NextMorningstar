package com.morningstar.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Objects;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestGithubUtil {
    private final GithubUtil githubUtil;

    @Value("${morningstar.github.token}")
    private String token;

    private final RestTemplate restTemplate;

    @Test
    public void testGetPublicRepo() {
        JsonNode result = githubUtil.getPublicRepo("HenryJi529", "ImageHosting");
        System.out.println(result);
        System.out.println(result.get("full_name").asText());
    }

    @Test
    public void testListPublicRepos(){
        System.out.println(githubUtil.listPublicRepos("HenryJi529"));
    }

    @Test
    public void testCreateAndDeleteRepo(){
        System.out.println(githubUtil.createPublicRepo(token, "test"));
        System.out.println(githubUtil.deleteRepo(token, "test"));
    }

    @Test
    public void testGetUser(){
        JsonNode result = githubUtil.getUser(token);
        System.out.println(result);
        System.out.println(result.get("id").asText());
        System.out.println(result.get("login").asText());
        System.out.println(result.get("email").asText());
        System.out.println(result.get("avatar_url").asText());
    }

    @Test
    public void testManageFile(){
        String repoName = "ImageHosting";
        String filename = "test/" + new Date().getTime() + ".jpg";
        byte[] content = Objects.requireNonNull(restTemplate.getForObject("https://picsum.photos/200/300", byte[].class));

        System.out.println(githubUtil.createFile(token, repoName, filename, content));
        System.out.println(githubUtil.deleteFile(token, repoName, filename));
    }
}
