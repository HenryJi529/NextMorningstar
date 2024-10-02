package com.morningstar.exception;

import cn.hutool.http.HttpStatus;

public class BlogTagNotFoundException extends BaseException {
    public <T> BlogTagNotFoundException(T tag) {
        super(String.format("标签[%s]不存在", tag));
        this.setCode(-HttpStatus.HTTP_NOT_FOUND);
    }
}
