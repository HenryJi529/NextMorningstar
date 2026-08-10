package com.morningstar.dev.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    private Source source;

    private Metadata metadata;

    private String title; // sonar中对应的是message

    /* 影响视角 */
    private Severity reliabilitySeverity; // BUG(逻辑缺陷)
    private Severity securitySeverity; // SECURITY(安全漏洞)
    private Severity maintainabilitySeverity; // MAINTAINABILITY(代码异味)

    private Integer effortInMinutes; // 修复耗时估算,单位为分钟

    private Status status;

    private String commitSha;

    private CommitMessage commitMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public enum Source {
        SONAR,
        AI;

        public static final String SONAR_NAME = "SONAR";
        public static final String AI_NAME = "AI";
    }

    public enum Severity {
        BLOCKER,
        HIGH,
        MEDIUM,
        LOW,
        INFO
    }

    public enum Status {
        SELECTED,
        FIXED, // 修复成功
        VERIFIED, // 验证成功
        ACCEPTED,
        REJECTED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommitMessage {
        private String subject; // 本次修复一句话总结（→ commit 第一行）
        private String body; // 修复思路与具体改动（→ commit 正文）
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@source")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SonarMetadata.class, name = Source.SONAR_NAME),
            @JsonSubTypes.Type(value = AiMetadata.class, name = Source.AI_NAME),
    })
    public static class Metadata {
        private String description; // 对应 sonar 的 introduction + root_cause
        private String suggestion; // 对应sonar how can I fix it
        private String filePath; // 文件路径
        private String codeSnippet; // 代码片段
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class SonarMetadata extends Metadata {
        private String issueKey;
        private String ruleKey;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class AiMetadata extends Metadata {
        private Type type; // 根因视角

        @Getter
        public enum Type {
            // ===== 架构设计 =====
            GOD_CLASS("上帝类：单个类承担过多职责，违反单一职责原则"),
            LONG_METHOD("过长方法：方法体过长，应拆分为多个小方法"),
            DEEP_NESTING("深层嵌套：if/for/while 嵌套过深，可读性差"),
            CIRCULAR_DEPENDENCY("循环依赖：两个或多个类相互依赖，耦合过高"),
            MISSING_ABSTRACTION("缺少抽象层：直接操作底层实现，未通过接口隔离"),

            // ===== 逻辑缺陷 =====
            INCONSISTENT_STATE("状态不一致：对象状态在不同路径下未保持一致性约束"),
            MISSING_VALIDATION("缺少输入校验：外部输入未做空值/范围/格式校验"),
            OFF_BY_ONE("边界错误：循环或索引差一位导致的逻辑错误"),
            MISSING_ERROR_HANDLING("缺少异常处理：吞异常或空 catch 块"),

            // ===== 安全漏洞 =====
            MISSING_RATE_LIMIT("缺少限流：接口或操作未限制调用频率，可被滥用"),
            INSECURE_DESERIALIZATION("不安全反序列化：反序列化未做类型白名单校验"),

            // ===== 可维护性 =====
            DUPLICATE_LOGIC("语义重复：不同位置的代码做同一件事，但非字面重复"),
            COMMENT_ROT("注释与代码不一致：代码已变但注释未同步更新"),
            HARDCODED_CONFIG("硬编码配置值：环境相关常量写在代码里而非配置中"),
            MAGIC_NUMBER("魔法数字：语义不明的数字字面量"),

            // ===== 性能瓶颈 =====
            N_PLUS_ONE_QUERY("N+1 查询：循环内逐条查询数据库，应批量查询"),
            UNNECESSARY_ALLOCATION("不必要的对象创建：循环内或高频路径上重复创建对象"),

            // ===== 并发风险 =====
            RACE_CONDITION("竞态条件：多线程操作之间的时序窗口导致数据不一致"),
            DEADLOCK_RISK("死锁风险：多锁获取顺序不一致可能导致死锁"),
            SHARED_MUTABLE_STATE("共享可变状态：多线程访问可变对象未做同步保护"),

            // ===== 兜底类型 =====
            OTHER("其他问题：AI 识别出的不在上述分类中的问题"),
            ;

            private final String description;

            Type(String description) {
                this.description = description;
            }

        }

    }
}
