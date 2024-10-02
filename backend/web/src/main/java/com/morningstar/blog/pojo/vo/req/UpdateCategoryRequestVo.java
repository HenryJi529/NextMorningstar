package com.morningstar.blog.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新博客分类请求对象")
public class UpdateCategoryRequestVo {
    @NotNull(message = "分类id不能为空")
    @Schema(description = "分类id")
    private Integer id;

    @NotBlank(message = "分类名不能为空")
    @Schema(description = "分类名")
    private String name;
}