package com.morningstar.exception;

import cn.hutool.http.HttpStatus;

public class PicGithubPatUnsetException extends BaseException {
    public PicGithubPatUnsetException() {
        super("Github Pat尚未设置");
        this.setCode(-HttpStatus.HTTP_NOT_FOUND);
    }
}
