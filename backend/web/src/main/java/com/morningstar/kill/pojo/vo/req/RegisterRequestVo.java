package com.morningstar.kill.pojo.vo.req;

import com.morningstar.kill.validation.constraint.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "注册请求对象")
@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterRequestVo extends CaptchaProtectedRequestVo {
    @Size(min = 5, max = 32, message = "用户名最短为5个字符，最长为32个字符")
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码不得小于6个字符")
    @Schema(description = "密码")
    private String password;

    @Size(min = 4, max = 32, message = "昵称最短为4个字符，最长为32个字符")
    @NotBlank(message = "昵称不能为空")
    @Schema(description = "昵称")
    private String nickname;

    @Sex(message = "性别必须为`男`或`女`")
    @NotBlank(message = "性别不能为空")
    @Schema(description = "性别")
    private String sex;
}
