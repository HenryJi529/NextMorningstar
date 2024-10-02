package com.morningstar.common.service.impl;

import com.morningstar.common.dao.mapper.PermissionMapper;
import com.morningstar.common.dao.mapper.RoleMapper;
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.pojo.bo.LoginUser;
import com.morningstar.common.pojo.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    private final PermissionMapper permissionMapper;

    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据username查询用户信息
        User dbUser = userMapper.selectByUsername(username);
        if (dbUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 查询权限信息
        Set<String> permissions = permissionMapper.selectTagByUserId(dbUser.getId());

        // 查询角色信息
        Set<String> roles = roleMapper.selectTagByUserId(dbUser.getId());
        permissions.addAll(roles.stream().map(role -> "ROLE_" + role).collect(Collectors.toSet()));

        return new LoginUser(dbUser, permissions);
    }
}
