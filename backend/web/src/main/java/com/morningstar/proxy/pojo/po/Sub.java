package com.morningstar.proxy.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("proxy_sub")
public class Sub {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String link;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;
}