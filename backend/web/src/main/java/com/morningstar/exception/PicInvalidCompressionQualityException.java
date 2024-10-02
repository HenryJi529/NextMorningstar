package com.morningstar.exception;

public class PicInvalidCompressionQualityException extends BaseException {
    public PicInvalidCompressionQualityException(Float compressionQuality) {
        super(String.format("压缩质量[%.2f]不在(0, 1]的范围内", compressionQuality));
    }
}
