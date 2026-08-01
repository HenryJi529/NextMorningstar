package com.morningstar.dev.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("dev_issue")
public class Issue {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private UUID runId;

    private String sonarProjectKey;

    private String sonarIssueKey;

    private String sonarRuleKey;

    private Severity sonarSeverity;

    private Type sonarType;

    private String sonarMessage;

    private String sonarEffort; // 修复耗时估算,如 30min

    private Status status;

    private String commitSha;

    private String commitMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public enum Severity {
        BLOCKER,
        CRITICAL,
        MAJOR,
        MINOR,
        INFO
    }

    public enum Type {
        BUG, // 缺陷, RELIABILITY
        VULNERABILITY, // 安全漏洞, SECURITY
        CODE_SMELL, // 代码异味, MAINTAINABILITY
    }

    public enum Status {
        SELECTED,
        FIXED,
        FAILED,
        ACCEPTED,
        REJECTED
    }
}
