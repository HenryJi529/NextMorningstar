package com.morningstar.exception;

public class PicImageInvalidFilenameException extends BaseException {
    public PicImageInvalidFilenameException(String filename) {
        super(String.format("图片文件名[%s]不合法", filename));
    }
}
