package com.morningstar.exception;

public class PicImageUploadingException extends BaseException {
    public PicImageUploadingException(String filename) {
        super(String.format("图片[%s]上传失败", filename));
    }
}
