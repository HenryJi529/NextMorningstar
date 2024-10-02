package com.morningstar.common.pojo.vo.req;

import com.morningstar.validation.constraint.FieldMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "更新密码请求对象")
@Data
@FieldMatch(field1 = "newPassword", field2 = "confirmPassword", message = "密码不匹配")
public class UpdatePasswordRequestVo {
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码不得小于6个字符")
    @Schema(description = "新密码")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码")
    private String confirmPassword;
}
