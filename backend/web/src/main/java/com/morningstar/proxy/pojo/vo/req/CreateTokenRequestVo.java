package com.morningstar.proxy.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建代理令牌请求对象")
public class CreateTokenRequestVo {
    @NotBlank(message = "令牌名不能为空")
    @Schema(description = "令牌名")
    private String name;

    @NotNull(message = "令牌值不能为空")
    @Schema(description = "令牌值")
    private String value;
}
