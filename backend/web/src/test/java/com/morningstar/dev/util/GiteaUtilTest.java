package com.morningstar.dev.util;

import com.morningstar.dev.pojo.bo.RepoIdentity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
class GiteaUtilTest {
    private final GiteaUtil giteaUtil;

    private final String repoLink = "http://127.0.0.1:7001/SpiderMan/test-repo";

    @Test
    void testParseRepoIdentity() {
        RepoIdentity repoIdentity = giteaUtil.parseRepoIdentity(repoLink);
        log.info("repoIdentity: {}", repoIdentity);
    }

    @Test
    void testIsCollaborator() {
        if (giteaUtil.isCollaborator(repoLink)) {
            log.info("is collaborator");
        } else {
            log.info("not collaborator");
        }
    }

    @Test
    void testAddCollaborator() {
        giteaUtil.addCollaborator(repoLink);
        log.info("add collaborator");
    }

    @Test
    void testRemoveCollaborator() {
        giteaUtil.removeCollaborator(repoLink);
        log.info("remove collaborator");
    }
}
