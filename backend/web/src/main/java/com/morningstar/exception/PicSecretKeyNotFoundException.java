package com.morningstar.exception;

public class PicSecretKeyNotFoundException extends BaseException {
    public PicSecretKeyNotFoundException(String secretKey) {
        super(String.format("secretKey[%s]不存在", secretKey));
    }
}
