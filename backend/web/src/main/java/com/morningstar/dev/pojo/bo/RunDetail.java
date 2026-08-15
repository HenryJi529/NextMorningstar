package com.morningstar.dev.pojo.bo;

import com.morningstar.dev.pojo.po.Run;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RunDetail extends Run {
    private String projectName;
    private String prLink;
    private Integer deliveredIssueCount;
}
