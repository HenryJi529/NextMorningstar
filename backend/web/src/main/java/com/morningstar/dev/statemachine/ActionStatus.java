package com.morningstar.dev.statemachine;

import lombok.Getter;

@Getter
public enum ActionStatus {
    SUCCEEDED,
    FAILED,
    RUNNING,
}
