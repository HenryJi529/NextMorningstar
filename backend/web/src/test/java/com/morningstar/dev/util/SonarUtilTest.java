package com.morningstar.dev.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SonarUtilTest {
    private final SonarUtil sonarUtil;

    @Test
    public void testGetSonarRuleByKey() {
        log.info(sonarUtil.getSonarRuleByKey("xml:S125").toString());
        log.info(sonarUtil.getSonarRuleByKey("java:S8688").toString());
    }

    @Test
    public void testGetSonarIssueByKey() {
        log.info(sonarUtil.getSonarIssueByKey("b645efd3-6a5a-4760-964d-a264551aac0f").toString());
        log.info(sonarUtil.getSonarIssueByKey("aa5514b7-c0c3-48e1-a19d-15049036ead5").toString());
    }

    @Test
    public void testGetAllOpenSonarIssuesByProjectKey() {
        System.out.println(sonarUtil.getAllOpenSonarIssuesByProjectKey("tester:smartunionhub").size());
    }
}
