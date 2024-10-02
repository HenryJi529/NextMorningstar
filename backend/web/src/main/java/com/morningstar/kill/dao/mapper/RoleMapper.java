package com.morningstar.kill.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.kill.pojo.po.Role;
import io.lettuce.core.dynamic.annotation.Param;

/**
* @author henry529
* @description 针对表【role】的数据库操作Mapper
* @Entity com.morningstar.pojo.entity.Role
*/
public interface RoleMapper extends BaseMapper<Role> {
    void truncate();

    long selectCount();

    Role selectByTag(@Param("tag") String tag);
}
