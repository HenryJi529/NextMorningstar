package com.morningstar.common.pojo.vo.req;

import com.morningstar.common.pojo.po.SysParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "系统参数创建请求对象")
@Data
public class CreateSysParamRequestVo {
    @Schema(description = "名称")
    private String name;

    @Schema(description = "内容")
    private String value;

    @Schema(description = "作用域")
    private SysParam.Scope scope;

    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "备注")
    private String remark;
}
