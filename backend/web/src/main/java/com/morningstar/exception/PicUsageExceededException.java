package com.morningstar.exception;

public class PicUsageExceededException extends BaseException {
    public PicUsageExceededException() {
        super("超过使用天数限额，无法继续上传图片");
    }
}