package com.morningstar.common.pojo.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "登出响应对象")
public class LogoutResponseVo {
    @Schema(description = "用户名")
    private String username;
}
