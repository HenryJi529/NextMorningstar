package com.morningstar.common.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sys_param")
public class SysParam {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String value;

    private Scope scope;

    private Boolean status;

    private String remark;

    public enum Scope {
        PUBLIC,
        INTERNAL
    }

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
