package com.morningstar.dev.statemachine.result;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.morningstar.dev.statemachine.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActionResult.class, name = Action.Type.BASE_NAME),
        @JsonSubTypes.Type(value = StartResult.class, name = Action.Type.START_NAME),
        @JsonSubTypes.Type(value = SyncResult.class, name = Action.Type.SYNC_NAME),
        @JsonSubTypes.Type(value = ScanResult.class, name = Action.Type.SCAN_NAME),
        @JsonSubTypes.Type(value = FixResult.class, name = Action.Type.FIX_NAME),
        @JsonSubTypes.Type(value = VerifyResult.class, name = Action.Type.VERIFY_NAME),
        @JsonSubTypes.Type(value = SubmitResult.class, name = Action.Type.SUBMIT_NAME),
        @JsonSubTypes.Type(value = RestoreResult.class, name = Action.Type.RESTORE_NAME),
        @JsonSubTypes.Type(value = CleanResult.class, name = Action.Type.CLEAN_NAME),
})
public class ActionResult {
    private Status status;
    private String message;

    public enum Status {
        SUCCEEDED,
        FAILED,
    }
}
