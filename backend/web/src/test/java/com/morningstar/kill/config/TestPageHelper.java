package com.morningstar.kill.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.morningstar.kill.dao.mapper.UserMapper;
import com.morningstar.kill.pojo.po.User;
import com.morningstar.kill.pojo.vo.resp.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class TestPageHelper {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() throws JsonProcessingException {
        int page = 1;
        int pageSize = 10;
        PageHelper.startPage(page, pageSize);

        List<User> all = userMapper.selectAll();
        // 封装查询结果到pageInfo
        PageInfo<User> pageInfo = new PageInfo<>(all);

        //获取分页的详情数据
        log.info("获级当前页: {}", pageInfo.getPageNum());
        log.info("获级总页数: {}", pageInfo.getPages());
        log.info("每页大小: {}", pageInfo.getPageSize());
        log.info("当前页的记录数: {}", pageInfo.getSize());
        log.info("总记录数: {}", pageInfo.getTotal());
        List<User> list = pageInfo.getList(); //获级当前页的内容
        log.info(list.toString());

        // 封装成PageResult
        PageResult<User> userPageResult = new PageResult<>(pageInfo);
        log.info(new ObjectMapper().writeValueAsString(userPageResult));
    }
}