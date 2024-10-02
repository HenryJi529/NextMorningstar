package com.morningstar.common.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "更新头像请求对象")
@Data
public class UpdateAvatarRequestVo {
    @NotBlank(message = "头像不能为空")
    @Schema(description = "头像")
    private String avatar;
}
