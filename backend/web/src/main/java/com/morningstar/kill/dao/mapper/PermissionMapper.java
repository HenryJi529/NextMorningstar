package com.morningstar.kill.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.kill.pojo.po.Permission;
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

    void truncate();

    long selectCount();

    Permission selectByTag(@Param("tag") String tag);
}
