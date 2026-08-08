package com.morningstar.dev.dao.type;

import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.infra.dao.type.JsonTypeHandler;
import org.springframework.stereotype.Component;

@Component
public class IssueMetadataTypeHandler extends JsonTypeHandler<Issue.Metadata> {
    public IssueMetadataTypeHandler() {
        super(Issue.Metadata.class);
    }
}