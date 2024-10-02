package com.morningstar.common.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.morningstar.common.pojo.po.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectRandomN() {
        List<User> users = userMapper.selectRandomN(10);
        System.out.println(Arrays.toString(users.stream().map(User::getUsername).toArray()));
    }

    @Test
    public void testSelectPage() {
        int current = 1; // 当前页码
        int size = 2; // 每页显示条数
        IPage<User> page = new Page<>(current, size);
        userMapper.selectPage(page, null);
        List<User> records = page.getRecords();//当前页的数据
        long pages = page.getPages(); //总页数
        long total = page.getTotal(); //总记录数
        System.out.println(records.size());
        System.out.println(pages);
        System.out.println(total);
    }

    @Test
    public void testSelectAllByPage() {
        IPage<User> page = new Page<>(4, 10);
        userMapper.selectAllByPage(page);
        System.out.println(page.getRecords().size());
    }

    @Test
    public void testLambdaQueryWrapper() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "henry");
        List<User> users = userMapper.selectList(wrapper);
        System.out.println(users);
    }

    @Test
    public void testAutoFill() {
        String username = "晨星小站自由民" + new Random().nextInt(1000);
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .password("PC9527")
                .nickname(username)
                .build();
        userMapper.insert(user);

        System.out.println(userMapper.selectByUsername(username));
    }
}
