package com.morningstar.dev.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("dev_project")
public class Project {
    @TableId("id")
    private UUID id;

    private String link;

    private String description;

    private Integer maxFixesPerRun;
}
