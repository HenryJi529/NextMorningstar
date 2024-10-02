package com.morningstar.proxy.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "更新代理订阅请求对象")
public class UpdateSubRequestVo {
    @NotNull(message = "订阅id不能为空")
    @Schema(description = "订阅id")
    private Integer id;

    @Schema(description = "订阅名")
    private String name;

    @Schema(description = "链接")
    private String link;
}
