package com.morningstar.dev.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.morningstar.dev.statemachine.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("dev_run")
public class Run {
    @TableId("id")
    private UUID id;

    private UUID projectId;

    private State state;

    // NOTE: 观测变量
    private Status status;

    public enum Status {
        RUNNING,
        SUCCEEDED,
        FAILED,
        CANCELING,
        CANCELED,
    }
}
