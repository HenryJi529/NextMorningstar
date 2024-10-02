package com.morningstar.kill.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TableName: permission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName("permission")
public class Permission {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String tag;

    private Boolean status;

    private String path;

    private String remark;
}
