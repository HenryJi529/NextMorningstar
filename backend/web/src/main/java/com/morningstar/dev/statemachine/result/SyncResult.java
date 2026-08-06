package com.morningstar.dev.statemachine.result;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SyncResult extends ActionResult {
    private String gitUrl;
    private String branchName;
    private String commitSha;
}
