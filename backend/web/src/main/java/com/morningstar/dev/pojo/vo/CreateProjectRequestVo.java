package com.morningstar.dev.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建研发项目请求对象")
public class CreateProjectRequestVo {
    @NotBlank(message = "项目名不能为空")
    @Schema(description = "项目名")
    private String name;

    @NotBlank(message = "仓库链接不能为空")
    @Schema(description = "仓库链接")
    private String link;

    @NotBlank(message = "分支名不能为空")
    @Schema(description = "分支名")
    private String branchName;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "单次最大Sonar问题数")
    private Integer maxSonarIssuesPerRun;

    @Schema(description = "单次最大AI问题数")
    private Integer maxAiIssuesPerRun;

    @Schema(description = "管理员ID", hidden = true)
    private UUID adminId;
}
