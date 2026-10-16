package com.morningstar.dev.pojo.bo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.morningstar.dev.pojo.po.Issue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiIssue {
    private String title;
    private String filePath;
    private String codeSnippet;
    private Integer startLine;
    private Integer endLine;
    private Issue.AiMetadata.Type type;
    private EffortLevel effortInMinutes;
    private String description;
    private String suggestion;
    private Issue.Severity reliabilitySeverity;
    private Issue.Severity securitySeverity;
    private Issue.Severity maintainabilitySeverity;

    // AI 通道修复工时估算(封闭档位)
    @Getter
    public enum EffortLevel {
        MIN_5(5, "零上下文微改：删除冗余代码、修正拼写、补格式等，看完即改"),
        MIN_15(15, "单行级小改：简单理解上下文后修改一两行，如补判空、换用推荐 API"),
        MIN_30(30, "单文件内的局部修改：读懂方法逻辑后改一处实现"),
        MIN_60(60, "单文件结构性修改，或涉及 2-3 个文件的联动修改"),
        MIN_120(120, "跨多文件重构：需理清上下游依赖关系后动手"),
        MIN_240(240, "大范围结构调整：涉及模块级设计，需充分理解后分步实施"),
        ;

        private final int minutes;
        private final String description;

        EffortLevel(int minutes, String description) {
            this.minutes = minutes;
            this.description = description;
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static EffortLevel fromMinutes(int minutes) {
            for (EffortLevel level : values()) {
                if (level.minutes == minutes) {
                    return level;
                }
            }
            throw new IllegalArgumentException("非法 effortInMinutes: " + minutes);
        }
    }
}
