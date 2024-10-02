package com.morningstar.kill.initializer;

import com.morningstar.kill.dao.mapper.PermissionMapper;
import com.morningstar.kill.dao.mapper.RoleMapper;
import com.morningstar.kill.dao.mapper.RolePermissionMapper;
import com.morningstar.kill.pojo.po.Permission;
import com.morningstar.kill.pojo.po.Role;
import com.morningstar.kill.pojo.po.RolePermission;
import com.morningstar.kill.properties.InitProperties;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

public abstract class CommonDataInitializer {
    @Autowired
    private InitProperties initProperties;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    public void initializeData() {
        System.out.println(ansi().eraseScreen().fg(GREEN).a("Initializing data...").reset());
        System.out.println(ansi().eraseScreen().fg(GREEN).a("Initializing user...").reset());
        initializeUser();
        System.out.println(ansi().eraseScreen().fg(GREEN).a("Initializing role...").reset());
        initializeRole();
        System.out.println(ansi().eraseScreen().fg(GREEN).a("Initializing permission...").reset());
        initializePermission();
        System.out.println(ansi().eraseScreen().fg(GREEN).a("Initializing role_permission").reset());
        initializeRolePermission();
        System.out.println(ansi().eraseScreen().fg(GREEN).a("Initializing user_role").reset());
        initializeUserRole();
        System.out.println(ansi().eraseScreen().fg(GREEN).a("Initializing record").reset());
        initializeRecord();
    }

    protected abstract void initializeUser();

    protected void initializeRole(){
        if(roleMapper.selectCount() == 0){
            for(Role role: initProperties.getRole()){
                roleMapper.insert(Role.builder().name(role.getName()).status(role.getStatus()).tag(role.getTag()).build());
            }
        }
    }

    protected void initializePermission(){
        if(permissionMapper.selectCount() == 0){
            for(Permission permission: initProperties.getPermission()){
                permissionMapper.insert(Permission.builder().name(permission.getName()).status(permission.getStatus()).tag(permission.getTag()).build());
            }
        }
    }

    protected void initializeRolePermission(){
        if(rolePermissionMapper.selectCount() == 0){
            for(String roleTag: initProperties.getRolePermission().keySet()){
                for(String permissionTag: initProperties.getRolePermission().get(roleTag)){
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

    protected abstract void initializeUserRole();

    protected abstract void initializeRecord();
}
