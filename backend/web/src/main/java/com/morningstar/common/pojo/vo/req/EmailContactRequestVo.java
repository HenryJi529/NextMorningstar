package com.morningstar.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailContactRequestVo {
    @Email(message = "邮箱必须有效")
    @NotBlank(message = "邮箱不能为空")
    @Schema(description = "邮箱")
    private String email;

    @NotBlank(message = "主题不能为空")
    @Schema(description = "主题")
    private String subject;

    @NotBlank(message = "内容不能为空")
    @Size(min = 10, message = "内容不得小于10个字符")
    @Schema(description = "内容")
    private String content;
}
