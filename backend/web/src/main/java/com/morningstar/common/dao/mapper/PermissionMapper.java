package com.morningstar.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.common.pojo.po.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.Set;
import java.util.UUID;

/**
 * @author henry529
 * @description 针对表【permission】的数据库操作Mapper
 * @Entity com.morningstar.pojo.entity.Permission
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    Set<String> selectTagByUserId(@Param("userId") UUID userId);

    Permission selectByTag(@Param("tag") String tag);
}
