package com.morningstar.initializer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.common.dao.mapper.*;
import com.morningstar.common.pojo.bo.UserRolePair;
import com.morningstar.common.pojo.po.Permission;
import com.morningstar.common.pojo.po.Role;
import com.morningstar.common.pojo.po.RolePermission;
import com.morningstar.common.pojo.po.User;
import com.morningstar.constant.ElasticSearchConstant;
import com.morningstar.properties.AccountProperties;
import com.morningstar.properties.PermProperties;
import com.morningstar.util.CopyUtil;
import com.morningstar.util.EsUtil;
import com.morningstar.util.FakerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;


public abstract class CommonInitializer {
    @Autowired
    private PermProperties permProperties;

    @Autowired
    private FakerUtil fakerUtil;

    @Autowired
    private AccountProperties accountProperties;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private EsUtil esUtil;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    private void initializePerm() {
        for (Role role : permProperties.getRole()) {
            if (roleMapper.selectByTag(role.getTag()) == null) {
                roleMapper.insert(role);
            }
        }
        for (Permission permission : permProperties.getPermission()) {
            if (permissionMapper.selectByTag(permission.getTag()) == null) {
                permissionMapper.insert(permission);
            }
        }
        for (String roleTag : permProperties.getRolePermission().keySet()) {
            for (String permissionTag : permProperties.getRolePermission().get(roleTag)) {
                Role role = roleMapper.selectByTag(roleTag);
                Permission permission = permissionMapper.selectByTag(permissionTag);
                LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(RolePermission::getRoleId, role.getId());
                wrapper.eq(RolePermission::getPermissionId, permission.getId());
                if (rolePermissionMapper.selectCount(wrapper) == 0) {
                    rolePermissionMapper.insert(
                            new RolePermission(
                                    role.getId(),
                                    permission.getId()
                            )
                    );
                }
            }
        }
    }

    protected void initializeAccount() {
        // 添加默认账号
        for (User user : accountProperties.getUser()) {
            if (userMapper.selectByUsername(user.getUsername()) == null) {
                User dbUser = fakerUtil.fakeUser();
                CopyUtil.copyNonNullProperties(user, dbUser);
                dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
                userMapper.insert(dbUser);
                System.out.printf("user: 创建%s...%n", user.getUsername());
            }
        }
        for (UserRolePair userRolePair : accountProperties.getUserRole()) {
            if (!userRoleMapper.exist(userRolePair)) {
                userRoleMapper.insert(userRolePair);
                System.out.printf("user-role: 创建 %s-%s...%n", userRolePair.getUsername(), userRolePair.getRoleTag());
            }
        }
    }

    protected abstract void initializeKill();

    protected void initializeBlog() {
        String indexName = ElasticSearchConstant.BLOG_ARTICLE_INDEX;
        if (!esUtil.existIndex(indexName)) {
            esUtil.createIndex(indexName);
        }
    }

    public void initialize() {
        initializePerm();
        System.out.println(ansi().fg(GREEN).a("权限相关表初始化完成...").reset());

        initializeAccount();
        System.out.println(ansi().fg(GREEN).a("账号相关表初始化完成...").reset());

        initializeKill();
        System.out.println(ansi().fg(GREEN).a("Kill应用初始化完成...").reset());

        initializeBlog();
        System.out.println(ansi().fg(GREEN).a("Blog应用初始化完成...").reset());
    }
}
