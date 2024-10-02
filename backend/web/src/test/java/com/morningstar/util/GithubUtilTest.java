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
import java.util.Random;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GithubUtilTest {
    private final GithubUtil githubUtil;
    private final String testRepoName = "TestRepo";
    private final RestTemplate restTemplate;
    @Value("${morningstar.github.token}")
    private String token;

    @Test
    public void testGetRepo() {
        JsonNode result = githubUtil.getRepo(token, testRepoName);
        System.out.println(result);
        System.out.println(result.get("full_name").asText());
        System.out.println(result.get("private").asText());
    }

    @Test
    public void testListRepos() {
        System.out.println(githubUtil.listRepos(token));
    }

    @Test
    public void testCreateAndDeleteRepo() {
        String repoName = "test" + (new Random()).nextInt(20);
        System.out.println(githubUtil.createPrivateRepo(token, repoName));
        System.out.println(githubUtil.deleteRepo(token, repoName));
        System.out.println(githubUtil.createPublicRepo(token, repoName));
        System.out.println(githubUtil.deleteRepo(token, repoName));
    }

    @Test
    public void testGetUser() {
        System.out.println(githubUtil.getUser(token));
        System.out.println(githubUtil.getUserAsAccount(token));
        System.out.println(githubUtil.getUser(token, "HenryJi529"));
    }

    @Test
    public void testManageFile() {
        String filename = "test/" + new Date().getTime() + ".jpg";
        byte[] content = Objects.requireNonNull(restTemplate.getForObject("https://picsum.photos/200/300", byte[].class));
        System.out.println("创建文件...");
        System.out.println(githubUtil.createFile(token, testRepoName, filename, content));
        System.out.println("遍历文件...");
        githubUtil.getFileOrDir(token, testRepoName, "test").forEach(System.out::println);
        System.out.println("删除文件...");
        System.out.println(githubUtil.deleteFile(token, testRepoName, filename));
    }
}
