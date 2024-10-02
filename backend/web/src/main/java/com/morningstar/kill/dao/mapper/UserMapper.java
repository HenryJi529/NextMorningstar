package com.morningstar.kill.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morningstar.kill.pojo.bo.UserDailyStats;
import com.morningstar.kill.pojo.bo.UserEditableInfo;
import com.morningstar.kill.pojo.po.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
* @author henry529
* @description 针对表【user】的数据库操作Mapper
* @Entity com.morningstar.pojo.entity.User
*/
public interface UserMapper extends BaseMapper<User> {
    User selectByUsername(@Param("username") String username);

    List<User> selectAll();

    IPage<User> selectAllByPage(IPage<User> page);

    long selectCount();

    UserEditableInfo selectEditableInfoByUsername(String username);

    void updatePasswordByUsername(@Param("password") String password, @Param("username") String username);

    void updateNicknameByUsername(@Param("nickname") String nickname, @Param("username") String username);

    void updateAvatarByUsername(@Param("avatar") String avatar, @Param("username") String username);

    List<User> selectRandomN(int n);

    List<UserDailyStats> selectRecentDailyStatsByUserId(@Param("n") int n, @Param("userId") UUID userId);
}
