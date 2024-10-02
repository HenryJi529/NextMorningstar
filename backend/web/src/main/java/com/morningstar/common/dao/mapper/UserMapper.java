package com.morningstar.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morningstar.common.pojo.po.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author henry529
* @description 针对表【user】的数据库操作Mapper
* @Entity com.morningstar.pojo.entity.User
*/
public interface UserMapper extends BaseMapper<User> {
    User selectByUsername(@Param("username") String username);

    List<User> selectByFuzzyValue(@Param("fuzzyValue") String fuzzyValue);

    IPage<User> selectAllByPage(IPage<User> page);

    List<User> selectRandomN(int n);
}
