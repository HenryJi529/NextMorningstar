package com.morningstar.exception;

public class PicImageInvalidContentTypeException extends BaseException {
    public PicImageInvalidContentTypeException(String filename, String contentType) {
        super(String.format("图片[%s]的Content-Type[%s]不合法", filename, contentType));
    }
}
