package com.morningstar.blog.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建博客评论请求对象")
public class CreateCommentRequestVo {
    @Schema(description = "回复的评论id")
    private Long parentId;

    @NotNull(message = "文章id不能为空")
    @Schema(description = "文章id")
    private Long articleId;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "内容")
    private String content;
}
