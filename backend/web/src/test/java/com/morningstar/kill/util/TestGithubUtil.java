package com.morningstar.kill.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestGithubUtil {
    @Autowired
    private GithubUtil githubUtil;

    @Test
    public void testGetRepo() {
        JsonNode result = githubUtil.getRepo();
        System.out.println(result.get("full_name").asText());
    }

    @Test
    public void testListPublicRepos(){
        System.out.println(githubUtil.listPublicRepos());
    }

    @Test
    public void testCreatePublicRepo(){
        System.out.println(githubUtil.createPublicRepo("test4"));
    }

    @Test
    public void testDeleteRepo(){
        System.out.println(githubUtil.deleteRepo("test3"));
    }
}
