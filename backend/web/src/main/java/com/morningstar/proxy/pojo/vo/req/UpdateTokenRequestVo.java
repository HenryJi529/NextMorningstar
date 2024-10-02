package com.morningstar.proxy.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新代理令牌请求对象")
public class UpdateTokenRequestVo {
    @NotNull(message = "令牌id不能为空")
    @Schema(description = "令牌id")
    private Integer id;

    @Schema(description = "令牌名")
    private String name;

    @Schema(description = "令牌值")
    private String value;
}
