package com.morningstar.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Schema(description = "登入请求对象")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class LoginRequestVo extends ImageCaptchaProtectedRequestVo {
    @NotBlank(message = "账号不能为空")
    @Schema(description = "账号")
    private String account;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}
