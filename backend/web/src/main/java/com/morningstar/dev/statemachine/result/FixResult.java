package com.morningstar.dev.statemachine.result;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FixResult extends ActionResult {
    private Integer fixedSonarIssueNum;
    private Integer fixedAiIssueNum;
}
