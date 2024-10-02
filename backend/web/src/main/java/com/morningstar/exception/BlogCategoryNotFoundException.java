package com.morningstar.exception;

import cn.hutool.http.HttpStatus;

public class BlogCategoryNotFoundException extends BaseException {
    public <T> BlogCategoryNotFoundException(T category) {
        super(String.format("分类[%s]不存在", category));
        this.setCode(-HttpStatus.HTTP_NOT_FOUND);
    }
}
