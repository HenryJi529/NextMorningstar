package com.morningstar.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morningstar.common.pojo.po.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    List<User> selectByFuzzyValue(@Param("fuzzyValue") String fuzzyValue);

    IPage<User> selectAllByPage(IPage<User> page);

    List<User> selectRandomN(int n);
}
