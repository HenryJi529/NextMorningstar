package com.morningstar.blog.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建博客标签请求对象")
public class CreateTagRequestVo {
    @NotBlank(message = "标签名不能为空")
    @Schema(description = "标签名")
    private String name;
}