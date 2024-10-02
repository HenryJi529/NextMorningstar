package com.morningstar.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.po.Article;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {
    List<Article> selectRandomN(int N);

    ArticleDetail selectArticleDetailByArticleId(@Param("articleId") long id);

    List<ArticleDetail> selectAllArticleDetail(@Param("limit") int limit, @Param("offset") int offset);

    List<ArticleDetail> selectArticleDetailByCategoryId(@Param("categoryId") int id, @Param("limit") int limit, @Param("offset") int offset);

    List<ArticleDetail> selectArticleDetailByTagId(@Param("tagId") int id, @Param("limit") int limit, @Param("offset") int offset);
}
