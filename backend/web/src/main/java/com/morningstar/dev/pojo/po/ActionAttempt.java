package com.morningstar.dev.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.ActionStatus;
import com.morningstar.dev.statemachine.result.ActionResult;
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
@TableName("dev_action_attempt")
public class ActionAttempt {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private UUID runId;

    private Action.Type actionType;

    private Integer attemptNo;

    private ActionStatus status;

    private ActionResult result;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime startTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime endTime;
}
