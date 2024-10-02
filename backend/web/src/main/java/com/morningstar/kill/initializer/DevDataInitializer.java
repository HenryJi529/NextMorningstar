package com.morningstar.kill.initializer;

import com.morningstar.kill.dao.mapper.*;
import com.morningstar.kill.pojo.po.*;
import com.morningstar.kill.pojo.po.Record;
import com.morningstar.kill.util.FakerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DevDataInitializer extends CommonDataInitializer{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private FakerUtil fakerUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    protected void initializeUser(){
        if(userMapper.selectByUsername("henry") == null){
            User adminUser = fakerUtil.fakeUser();
            adminUser.setUsername("henry");
            adminUser.setPassword(passwordEncoder.encode("1234asdw"));
            adminUser.setIsAdmin(true);
            adminUser.setNickname("河海小爬虫");
            adminUser.setSex("男");
            userMapper.insert(adminUser);
            System.out.println("user: 创建admin...");
        }
        if(userMapper.selectByUsername("sherry") == null){
            User test1User = fakerUtil.fakeUser();
            test1User.setUsername("sherry");
            test1User.setPassword(passwordEncoder.encode("123456"));
            test1User.setSex("女");
            userMapper.insert(test1User);
            System.out.println("user: 创建sherry");
        }
        long currentUserNum = userMapper.selectCount();
        while(currentUserNum < 100){
            try{
                User fakerUser = fakerUtil.fakeUser();
                userMapper.insert(fakerUser);
                currentUserNum++;
            }catch (DuplicateKeyException e){
                System.out.println(e.getMessage());
            }
        }
    }

    protected void initializeUserRole(){
        String[] usernames = new String[]{"henry", "sherry"};
        String[] roleTags = new String[]{"supervisor", "sponsor"};
        if(userRoleMapper.selectCount() < usernames.length){
            userRoleMapper.truncate();
            for(int i = 0; i < usernames.length; i++){
                userRoleMapper.insert(
                        new UserRole(
                                userMapper.selectByUsername(usernames[i]).getId(),
                                roleMapper.selectByTag(roleTags[i]).getId()
                        )
                );
            }
        }
    }

    protected void initializeRecord(){
        long currentRecordNum = recordMapper.selectCount();
        while(currentRecordNum < 10000){
            try{
                Record fakeRecord = fakerUtil.fakeRecord();
                recordMapper.insertRecordAndUserRecord(fakeRecord);
                currentRecordNum++;
            }catch (DuplicateKeyException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
