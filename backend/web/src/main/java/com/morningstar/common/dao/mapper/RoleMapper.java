package com.morningstar.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.common.pojo.po.Role;
import io.lettuce.core.dynamic.annotation.Param;

/**
* @description 针对表【role】的数据库操作Mapper
* @Entity com.morningstar.pojo.entity.Role
*/
public interface RoleMapper extends BaseMapper<Role> {
    Role selectByTag(@Param("tag") String tag);
}