package com.morningstar.common.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("sys_role_permission")
public class RolePermission {
    private Integer roleId;

    private Integer permissionId;
}
