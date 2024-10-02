package com.morningstar.blog.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("blog_comment")
public class Comment {
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    private UUID userId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;

    private String content;

    @TableField(fill = FieldFill.INSERT)
    private Timestamp createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;
}
