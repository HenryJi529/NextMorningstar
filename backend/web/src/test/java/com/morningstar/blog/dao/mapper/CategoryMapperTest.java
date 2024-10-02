package com.morningstar.blog.dao.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class CategoryMapperTest {
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    public void testSelectRandomN() {
        System.out.println(categoryMapper.selectRandomN(2));
    }

    @Test
    public void testSelectAllCategoryDetail() {
        System.out.println(categoryMapper.selectAllCategoryDetail());
    }
}
