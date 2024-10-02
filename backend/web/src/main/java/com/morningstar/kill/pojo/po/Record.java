package com.morningstar.kill.pojo.po;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.morningstar.kill.domain.game.GameLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TableName: record
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Record implements Serializable {
    @Schema(description = "记录id")
    @TableId("id")
    private UUID id;

    @Schema(description = "记录内容")
    private GameLog content;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Timestamp createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;
}