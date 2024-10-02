package com.morningstar.common.service;

import com.morningstar.common.pojo.vo.resp.ImageCaptchaResponseVo;

public interface CommonService {
    ImageCaptchaResponseVo getImageCaptcha();

    String getQiniuUploadToken();

    String getQrcode(String data);

    void getEmailCaptcha(String email);
}
