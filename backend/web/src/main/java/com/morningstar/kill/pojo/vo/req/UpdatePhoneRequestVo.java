package com.morningstar.kill.pojo.vo.req;


import com.morningstar.kill.validation.constraint.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "更新手机号请求对象")
@Data
public class UpdatePhoneRequestVo {
    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String code;

    @Phone(message = "手机号必须有效")
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号")
    private String phone;
}
