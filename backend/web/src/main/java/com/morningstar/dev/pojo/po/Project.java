package com.morningstar.dev.pojo.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("dev_project")
public class Project {
    @TableId("id")
    private UUID id;

    private UUID adminId;

    private String name;

    private String link;

    private String branchName;

    private String description;

    private Boolean enabled;

    private Integer maxSonarIssuesPerRun;

    private Integer maxAiIssuesPerRun;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
