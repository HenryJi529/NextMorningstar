package com.morningstar.exception;

import cn.hutool.http.HttpStatus;

public class BlogCommentNotFoundException extends BaseException {
    public <T> BlogCommentNotFoundException(T comment) {
        super(String.format("评论[%s]不存在", comment));
        this.setCode(-HttpStatus.HTTP_NOT_FOUND);
    }
}
