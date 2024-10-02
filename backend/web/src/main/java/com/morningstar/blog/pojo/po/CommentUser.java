package com.morningstar.blog.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
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
    private Long commentId;
    private UUID userId;
    private Boolean isApproved;
}
