package com.morningstar.common.service;

import com.morningstar.common.pojo.vo.resp.ImageCaptchaResponseVo;
import com.morningstar.constant.R;

public interface CommonService {
    R<ImageCaptchaResponseVo> getImageCaptcha();

    R<String> getQiniuUploadToken();

    R<String> getQrcode(String data);

    R<Object> getEmailCaptcha(String email);
}
