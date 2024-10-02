package com.morningstar.initializer;

import com.morningstar.common.dao.mapper.PermissionMapper;
import com.morningstar.common.dao.mapper.RoleMapper;
import com.morningstar.common.dao.mapper.RolePermissionMapper;
import com.morningstar.common.pojo.po.Permission;
import com.morningstar.common.pojo.po.Role;
import com.morningstar.common.pojo.po.RolePermission;
import com.morningstar.properties.InitAuthProperties;
import com.morningstar.util.EsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

public abstract class CommonDataInitializer {
    @Autowired
    private InitAuthProperties initAuthProperties;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private EsUtil esUtil;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    private void initializeAuth() {
        if(roleMapper.selectCount(null) == 0){
            for(Role role: initAuthProperties.getRole()){
                roleMapper.insert(Role.builder().name(role.getName()).status(role.getStatus()).tag(role.getTag()).build());
            }
        }
        if(permissionMapper.selectCount(null) == 0){
            for(Permission permission: initAuthProperties.getPermission()){
                permissionMapper.insert(Permission.builder().name(permission.getName()).status(permission.getStatus()).tag(permission.getTag()).build());
            }
        }
        if(rolePermissionMapper.selectCount(null) == 0){
            for(String roleTag: initAuthProperties.getRolePermission().keySet()){
                for(String permissionTag: initAuthProperties.getRolePermission().get(roleTag)){
                    rolePermissionMapper.insert(
                            new RolePermission(
                                    roleMapper.selectByTag(roleTag).getId(),
                                    permissionMapper.selectByTag(permissionTag).getId()
                            )
                    );
                }
            }
        }
    }

    protected abstract void initializeUser();

    protected abstract void initializeKill();

    protected void initializeBlog(){
        String indexName = "blog_article";
        if(!esUtil.existIndex(indexName)){
            esUtil.createIndex(indexName);
        }
    }

    public void initializeData() {
        System.out.println(ansi().eraseScreen().fg(GREEN).a("初始化权限相关表...").reset());
        initializeAuth();
        System.out.println(ansi().eraseScreen().fg(GREEN).a("初始化用户相关表...").reset());
        initializeUser();
        System.out.println(ansi().eraseScreen().fg(GREEN).a("初始化Kill应用...").reset());
        initializeKill();
        System.out.println(ansi().eraseScreen().fg(GREEN).a("初始化Blog应用...").reset());
        initializeBlog();
    }
}
