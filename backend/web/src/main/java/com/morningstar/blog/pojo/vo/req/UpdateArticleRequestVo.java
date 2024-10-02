package com.morningstar.blog.pojo.vo.req;

import com.morningstar.validation.constraint.Set;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新博客文章请求对象")
public class UpdateArticleRequestVo {
    @NotNull(message = "id不能为空")
    @Schema(description = "id")
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Schema(description = "标题")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "内容")
    private String content;

    @NotNull(message = "分类id不能为空")
    @Schema(description = "分类id")
    private Integer categoryId;

    @Set(message = "标签ids必须是集合")
    @Schema(description = "标签ids")
    @NotNull
    private List<Integer> tagIds;
}
