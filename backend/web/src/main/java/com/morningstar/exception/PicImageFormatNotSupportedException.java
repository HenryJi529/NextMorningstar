package com.morningstar.exception;

public class PicImageFormatNotSupportedException extends BaseException {
    public PicImageFormatNotSupportedException(String filename) {
        super(String.format("图片[%s]格式不支持", filename));
    }
}
