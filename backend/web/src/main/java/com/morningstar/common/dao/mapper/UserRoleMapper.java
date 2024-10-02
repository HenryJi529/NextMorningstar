package com.morningstar.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.common.pojo.bo.UserRolePair;
import com.morningstar.common.pojo.po.UserRole;
import org.apache.ibatis.annotations.Param;

/**
 * @description 针对表【user_role】的数据库操作Mapper
 * @Entity com.morningstar.pojo.entity.UserRole
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {
    void truncate();

    int exists(@Param("pair") UserRolePair pair);

    int insert(@Param("pair") UserRolePair pair);
}
