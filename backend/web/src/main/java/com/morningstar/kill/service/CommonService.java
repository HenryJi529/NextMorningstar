package com.morningstar.kill.service;

import com.morningstar.kill.pojo.vo.resp.CaptchaResponseVo;
import com.morningstar.kill.pojo.vo.resp.R;

public interface CommonService {
    R<CaptchaResponseVo> getCaptcha();

    R<String> getQiniuUploadToken();
}
