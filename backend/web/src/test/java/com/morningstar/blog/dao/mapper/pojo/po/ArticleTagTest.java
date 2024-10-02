package com.morningstar.blog.dao.mapper.pojo.po;

import com.morningstar.blog.pojo.po.ArticleTag;
import org.junit.jupiter.api.Test;

public class ArticleTagTest {
    @Test
    public void test() {
        ArticleTag articleTag1 = ArticleTag.builder().articleId(1L).tagId(2).build();
        ArticleTag articleTag2 = ArticleTag.builder().articleId(1L).tagId(2).build();

        System.out.println(articleTag1.equals(articleTag2));
    }
}
