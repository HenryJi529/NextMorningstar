package com.morningstar.common.service;

import com.morningstar.common.pojo.vo.resp.CaptchaResponseVo;
import com.morningstar.constant.R;

public interface CommonService {
    R<CaptchaResponseVo> getCaptcha();

    R<String> getQiniuUploadToken();
}
