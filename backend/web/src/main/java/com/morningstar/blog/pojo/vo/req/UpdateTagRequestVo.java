package com.morningstar.blog.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新博客标签请求对象")
public class UpdateTagRequestVo {
    @NotNull(message = "标签id不能为空")
    @Schema(description = "标签id")
    private Integer id;

    @NotBlank(message = "标签名不能为空")
    @Schema(description = "标签名")
    private String name;
}
