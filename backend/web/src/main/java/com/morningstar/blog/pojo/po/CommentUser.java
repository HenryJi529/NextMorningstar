package com.morningstar.blog.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("blog_comment_user")
public class CommentUser {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long commentId;
    private UUID userId;
    private Boolean isApproved;
}
