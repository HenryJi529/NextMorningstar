package com.morningstar.blog.service;

import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.vo.req.CreateArticleRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateArticleRequestVo;
import com.morningstar.constant.PageResult;

public interface ArticleService {
    ArticleDetail getArticleDetailByArticleId(Long id);

    PageResult<ArticleDetail> getAllArticleDetail(int pageNum, int pageSize);

    PageResult<ArticleDetail> getArticleDetailByCategoryId(int id, int pageNum, int pageSize);

    PageResult<ArticleDetail> getArticleDetailByTagId(int id, int pageNum, int pageSize);

    String createArticle(CreateArticleRequestVo vo);

    void updateArticle(UpdateArticleRequestVo vo);

    void deleteArticle(Long id);

    String getRandomArticleId();

    PageResult<ArticleDetail> getArticleDetailByTerm(String term, int pageNum, int pageSize);

    void refreshArticleIndex();
}
