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
 * TableName: role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("role")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String tag;

    private Boolean status;

    private String remark;
}
