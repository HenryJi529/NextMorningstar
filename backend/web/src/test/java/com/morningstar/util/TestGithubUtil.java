package com.morningstar.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestGithubUtil {
    private final GithubUtil githubUtil;

    @Test
    public void testGetConfiguredRepo() {
        JsonNode result = githubUtil.getConfiguredRepo();
        System.out.println(result.get("full_name").asText());
    }

    @Test
    public void testListPublicRepos(){
        System.out.println(githubUtil.listPublicRepos());
    }

    @Test
    public void testCreateAndDeleteRepo(){
        System.out.println(githubUtil.createPublicRepo("test"));
        System.out.println(githubUtil.deleteRepo("test"));
    }
}
