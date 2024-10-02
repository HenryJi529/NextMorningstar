package com.morningstar.proxy.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建代理节点请求对象")
public class CreateNodeRequestVo {
    @NotBlank(message = "链接不能为空")
    @Schema(description = "链接")
    private String link;
}
