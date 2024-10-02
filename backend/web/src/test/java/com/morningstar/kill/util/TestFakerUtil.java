package com.morningstar.kill.util;

import com.morningstar.kill.pojo.po.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestFakerUtil {

    @Autowired
    private FakerUtil fakerUtil;

    @Test
    public void test() {
        User[] users = new User[10];
        for(int i=0; i<users.length; i++) {
            users[i] = fakerUtil.fakeUser();
        }
        log.info(Arrays.toString(Arrays.stream(users).map(User::getNickname).toArray()));
        log.info(Arrays.toString(Arrays.stream(users).map(user -> String.format("%s-> %s", user.getCreateTime(), user.getUpdateTime())).toArray()));
    }
}
