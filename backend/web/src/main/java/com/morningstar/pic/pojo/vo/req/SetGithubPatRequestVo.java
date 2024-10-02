package com.morningstar.pic.pojo.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "设置GithubPat请求对象")
public class SetGithubPatRequestVo {
    @NotBlank(message = "GithubPat不能为空")
    @Schema(description = "GithubPat")
    private String pat;
}