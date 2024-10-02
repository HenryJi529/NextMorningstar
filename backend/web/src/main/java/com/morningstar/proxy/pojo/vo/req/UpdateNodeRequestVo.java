package com.morningstar.proxy.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "创建代理节点请求对象")
public class UpdateNodeRequestVo {
    @NotNull(message = "节点id不能为空")
    @Schema(description = "节点id")
    private Integer id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "链接")
    private String link;
}
