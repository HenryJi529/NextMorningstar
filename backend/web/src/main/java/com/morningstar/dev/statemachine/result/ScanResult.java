package com.morningstar.dev.statemachine.result;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ScanResult extends ActionResult {
    private Integer scannedSonarIssueNum;
    private Integer scannedAiIssueNum;
    private List<String> scannedSonarIssueKeys;
}
