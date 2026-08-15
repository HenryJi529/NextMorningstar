package com.morningstar.dev.pojo.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Stats {
    private Integer projectCount;

    private Integer enabledProjectCount;

    private Integer executingRunCount;

    private Integer pendingRunCount;

    private Integer maxConcurrency;

    /**
     * 成功结束的 run 交付的Issue总数(状态 VERIFIED/ACCEPTED/REJECTED)
     */
    private Integer deliveredIssueCount;

    /**
     * 已采纳(PR 合并)的 Issue 总数
     */
    private Integer acceptedIssueCount;

    /**
     * 提交过 PR 的 run 总数
     */
    private Integer prTotal;

    /**
     * PR 被合并的 run 总数
     */
    private Integer prMerged;

    /**
     * 累计节约人天
     */
    private Double savedPersonDays;
}
