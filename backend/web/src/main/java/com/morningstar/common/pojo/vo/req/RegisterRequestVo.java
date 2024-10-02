package com.morningstar.common.pojo.vo.req;

import com.morningstar.validation.constraint.FieldMatch;
import com.morningstar.validation.constraint.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "注册请求对象")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@FieldMatch(field1 = "password", field2 = "confirmPassword", message = "密码不匹配")
public class RegisterRequestVo extends ImageCaptchaProtectedRequestVo {
    @Size(min = 5, max = 32, message = "用户名最短为5个字符，最长为32个字符")
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码不得小于6个字符")
    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码")
    private String confirmPassword;

    @Size(min = 4, max = 32, message = "昵称最短为4个字符，最长为32个字符")
    @NotBlank(message = "昵称不能为空")
    @Schema(description = "昵称")
    private String nickname;

    @Sex(message = "性别必须为`男`或`女`")
    @NotBlank(message = "性别不能为空")
    @Schema(description = "性别")
    private String sex;
}
