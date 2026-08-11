package com.morningstar.dev.statemachine.result;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class VerifyResult extends ActionResult {
    private Integer verifiedSonarIssueNum;
    private Integer verifiedAiIssueNum;
}
