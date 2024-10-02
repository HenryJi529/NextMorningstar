package com.morningstar.common.pojo.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(description = "注册响应对象")
public class RegisterResponseVo {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "凭证")
    private String token;
}
