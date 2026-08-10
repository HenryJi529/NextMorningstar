package com.morningstar.dev.dao.type;

import com.morningstar.dev.pojo.po.Issue;
import com.morningstar.infra.dao.type.JsonTypeHandler;
import org.springframework.stereotype.Component;

@Component
public class CommitMessageTypeHandler extends JsonTypeHandler<Issue.CommitMessage> {
    public CommitMessageTypeHandler() {
        super(Issue.CommitMessage.class);
    }
}
