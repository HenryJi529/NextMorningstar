package com.morningstar.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Schema(description = "找回账号完成请求对象")
@Data
public class CompleteRecoveryRequestVo {
    @NotNull(message = "用户Id不能为空")
    @Schema(description = "用户Id")
    private UUID id;

    @NotBlank(message = "令牌不能为空")
    @Schema(description = "令牌")
    private String token;
}
