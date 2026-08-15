package com.morningstar.dev.pojo.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.morningstar.dev.statemachine.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@TableName("dev_run")
public class Run {
    @TableId("id")
    private UUID id;

    private UUID projectId;

    private TriggerType triggerType;

    private State state;

    // NOTE: 观测变量
    private Status status;

    private Integer prId;

    private PrStatus prStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public enum TriggerType {
        MANUAL,
        SCHEDULED,
    }

    public enum Status {
        RUNNING,
        SUCCEEDED,
        FAILED,
        CANCELING,
        CANCELED,
    }

    public enum PrStatus {
        OPEN,
        CLOSED,
        MERGED,
    }
}
