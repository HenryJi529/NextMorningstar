package com.morningstar.kill.service.impl;

import com.morningstar.kill.dao.mapper.PermissionMapper;
import com.morningstar.kill.dao.mapper.UserMapper;
import com.morningstar.kill.pojo.po.User;
import com.morningstar.kill.pojo.bo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper PermissionMapper;

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
