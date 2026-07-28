package com.morningstar.blog.dao.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class CategoryMapperTest {
    @Autowired
    private CategoryMapper categoryMapper;

    @Test
    void testSelectRandomN() {
        System.out.println(categoryMapper.selectRandomN(2));
    }

    @Test
    void testSelectAllCategoryDetail() {
        System.out.println(categoryMapper.selectAllCategoryDetail());
    }
}
