package com.morningstar.blog.service;

import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.vo.req.CreateArticleRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateArticleRequestVo;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;

public interface ArticleService {
    R<ArticleDetail> getArticleDetailByArticleId(Long id);

    R<PageResult<ArticleDetail>> getAllArticleDetail(int pageNum, int pageSize);

    R<PageResult<ArticleDetail>> getArticleDetailByCategoryId(int id, int pageNum, int pageSize);

    R<PageResult<ArticleDetail>> getArticleDetailByTagId(int id, int pageNum, int pageSize);

    R<Long> createArticle(CreateArticleRequestVo vo);

    R<Object> updateArticle(UpdateArticleRequestVo vo);

    R<Object> deleteArticle(Long id);

    R<String> getRandomArticleId();

    R<PageResult<ArticleDetail>> getArticleDetailByTerm(String term, int pageNum, int pageSize);

    R<Object> refreshArticle();
}
