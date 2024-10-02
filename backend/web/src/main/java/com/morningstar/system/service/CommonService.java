package com.morningstar.system.service;

import com.morningstar.system.pojo.vo.resp.ImageCaptchaResponseVo;

public interface CommonService {
    ImageCaptchaResponseVo getImageCaptcha();

    String getQiniuUploadToken();

    String getQrcode(String data);

    void getEmailCaptcha(String email);
}
