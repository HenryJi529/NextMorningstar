package com.morningstar.kill.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.kill.pojo.po.UserRole;

/**
 * @author henry529
 * @description 针对表【user_role】的数据库操作Mapper
 * @Entity com.morningstar.pojo.entity.UserRole
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {
    void truncate();

    long selectCount();
}
