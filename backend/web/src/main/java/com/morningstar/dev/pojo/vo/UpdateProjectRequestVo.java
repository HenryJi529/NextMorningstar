package com.morningstar.dev.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "更新研发项目请求对象")
public class UpdateProjectRequestVo {
    @NotNull(message = "项目ID不能为空")
    @Schema(description = "项目ID")
    private UUID id;

    @Schema(description = "项目名")
    private String name;

    @Schema(description = "分支名")
    private String branchName;

    @Schema(description = "项目描述")
    private String description;

    @Schema(description = "每轮修复的Sonar问题上限")
    @Min(value = 1, message = "每轮修复的Sonar问题上限不能小于1")
    private Integer maxSonarIssuesPerRun;

    @Schema(description = "每轮修复的AI问题上限")
    @Min(value = 1, message = "每轮修复的AI问题上限不能小于1")
    private Integer maxAiIssuesPerRun;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "管理员ID", hidden = true)
    private UUID adminId;
}
