package com.morningstar.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "更新昵称请求对象")
@Data
public class UpdateNicknameRequestVo {
    @Size(min = 4, max = 32, message = "昵称最短为4个字符，最长为32个字符")
    @NotBlank(message = "昵称不能为空")
    @Schema(description = "昵称")
    private String nickname;
}
