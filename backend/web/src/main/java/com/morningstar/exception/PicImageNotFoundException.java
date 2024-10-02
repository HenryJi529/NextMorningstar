package com.morningstar.exception;

import cn.hutool.http.HttpStatus;

public class PicImageNotFoundException extends BaseException {
    public PicImageNotFoundException(String path) {
        super(String.format("图片[%s]不存在", path));
        this.setCode(-HttpStatus.HTTP_NOT_FOUND);
    }
}
