package com.morningstar.util;

import com.morningstar.common.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@Slf4j
@SpringBootTest
public class FakerUtilTest {

    @Autowired
    private FakerUtil fakerUtil;

    @Test
    public void testFakeBlogCategory() {
        System.out.println(fakerUtil.fakeBlogCategory());
    }

    @Test
    public void testFakeBlogTag() {
        System.out.println(fakerUtil.fakeBlogTag());
    }

    @Test
    public void testFakeUser() {
        User[] users = new User[10];
        for (int i = 0; i < users.length; i++) {
            users[i] = fakerUtil.fakeUser();
        }
        System.out.println(Arrays.toString(Arrays.stream(users).map(User::getNickname).toArray()));
        System.out.println(Arrays.toString(Arrays.stream(users).map(user -> String.format("%s-> %s", user.getCreateTime(), user.getUpdateTime())).toArray()));
    }
}
