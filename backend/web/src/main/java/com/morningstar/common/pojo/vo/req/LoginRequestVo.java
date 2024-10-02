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
public class LoginRequestVo extends CaptchaProtectedRequestVo {
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;
}
