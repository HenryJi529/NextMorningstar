package com.morningstar.exception;

public class PicImageProcessingException extends BaseException {
    public PicImageProcessingException(String filename) {
        super(String.format("图片[%s]无法处理", filename));
    }
}
