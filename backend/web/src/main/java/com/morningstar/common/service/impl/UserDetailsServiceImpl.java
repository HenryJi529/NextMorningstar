package com.morningstar.common.service.impl;

import com.morningstar.common.dao.mapper.PermissionMapper;
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.pojo.po.User;
import com.morningstar.common.pojo.bo.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    private final PermissionMapper PermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据username查询用户信息
        User dbUser = userMapper.selectByUsername(username);
        if(dbUser == null){
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 根据userId查询权限信息
        Set<String> permissions = PermissionMapper.selectTagByUserId(dbUser.getId());
        return new LoginUser(dbUser, permissions);
    }
}
