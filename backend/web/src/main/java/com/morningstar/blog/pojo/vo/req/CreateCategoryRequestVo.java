package com.morningstar.blog.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建博客分类请求对象")
public class CreateCategoryRequestVo {
    @NotBlank(message = "分类名不能为空")
    @Schema(description = "分类名")
    private String name;
}
