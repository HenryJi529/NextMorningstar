package com.morningstar.dev.pojo.po;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class IssueTest {
    @Test
    public void testAiMetadataType() {
        log.info(Issue.AiMetadata.Type.COMMENT_ROT.getName());
        log.info(Issue.AiMetadata.Type.COMMENT_ROT.name());
    }
}
