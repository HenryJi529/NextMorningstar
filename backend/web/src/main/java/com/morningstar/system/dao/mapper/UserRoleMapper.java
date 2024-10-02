package com.morningstar.system.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.system.pojo.bo.UserRolePair;
import com.morningstar.system.pojo.po.UserRole;
import org.apache.ibatis.annotations.Param;

public interface UserRoleMapper extends BaseMapper<UserRole> {
    void truncate();

    boolean exist(@Param("pair") UserRolePair pair);

    int insert(@Param("pair") UserRolePair pair);
}
