package com.morningstar.kill.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.kill.pojo.po.RolePermission;

/**
 * @author henry529
 * @description 针对表【role_permission】的数据库操作Mapper
 * @Entity com.morningstar.pojo.entity.RolePermission
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    void truncate();
    
    long selectCount();
}
