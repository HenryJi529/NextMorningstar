package com.morningstar.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "找回账号发起请求对象")
@Data
public class InitiateRecoveryRequestVo {
    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String code;

    @Email(message = "邮箱必须有效")
    @NotBlank(message = "邮箱不能为空")
    @Schema(description = "邮箱")
    private String email;
}
