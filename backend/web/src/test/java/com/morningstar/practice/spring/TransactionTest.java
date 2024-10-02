package com.morningstar.practice.spring;

import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.pojo.po.User;
import com.morningstar.util.FakerUtil;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TransactionTest {

    private final TransactionClass transactionClass;

    private final UserMapper userMapper;

    @Test
    public void testTransaction() {
        int id = new Random().nextInt(100000);
        String userFailure = "test4failure" + id;
        String userSuccess = "test4success" + id;

        try {
            transactionClass.insertFail(userFailure);
        } catch (Exception e) {
            assert userMapper.selectByUsername(userFailure) == null;
        }

        transactionClass.insertSucceed(userSuccess);
        assert userMapper.selectByUsername(userSuccess) != null;
    }
}

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TransactionClass {
    private final UserMapper userMapper;

    private final FakerUtil fakerUtil;

    @Transactional
    public void insertFail(String username) {
        User fakeUser = fakerUtil.fakeUser();
        fakeUser.setUsername(username);
        userMapper.insert(fakeUser);

        throw new RuntimeException();
    }

    @Transactional
    public void insertSucceed(String username) {
        User fakeUser = fakerUtil.fakeUser();
        fakeUser.setUsername(username);
        userMapper.insert(fakeUser);
    }
}
