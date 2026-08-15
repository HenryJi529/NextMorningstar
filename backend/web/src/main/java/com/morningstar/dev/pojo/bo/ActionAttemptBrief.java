package com.morningstar.dev.pojo.bo;

import com.morningstar.dev.statemachine.Action;
import com.morningstar.dev.statemachine.ActionStatus;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ActionAttemptBrief {
    private Action.Type actionType;
    private Integer attemptNo;
    private ActionStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
