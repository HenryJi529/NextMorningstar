package com.morningstar.exception;

import cn.hutool.http.HttpStatus;

public class BlogArticleNotFoundException extends BaseException {
    public <T> BlogArticleNotFoundException(T article) {
        super(String.format("文章[%s]不存在", article));
        this.setCode(-HttpStatus.HTTP_NOT_FOUND);
    }
}
