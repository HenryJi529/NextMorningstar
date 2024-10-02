package com.morningstar.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public abstract class ImageCaptchaProtectedRequestVo {
    @NotBlank(message = "会话id不能为空")
    @Schema(description = "会话id")
    private String sessionId;

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String code;
}
