package com.morningstar.exception;

public class PicInvalidImageFilenameException extends BaseException {
    public PicInvalidImageFilenameException(String filename) {
        super(String.format("图片文件名[%s]不合法", filename));
    }
}
