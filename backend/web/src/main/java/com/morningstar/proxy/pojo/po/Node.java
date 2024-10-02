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
@TableName("proxy_node")
public class Node {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String link;

    private Integer subId;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;
}