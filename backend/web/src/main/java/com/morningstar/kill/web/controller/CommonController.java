package com.morningstar.kill.web.controller;


import com.morningstar.kill.pojo.vo.resp.CaptchaResponseVo;
import com.morningstar.kill.pojo.vo.resp.R;
import com.morningstar.kill.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通用功能相关接口定义")
@RestController
@RequestMapping("/api/common")
public class CommonController {
    @Autowired
    private CommonService commonService;

    /**
     * 验证码获取
     */
    @Operation(summary = "验证码获取")
    @GetMapping("/captcha")
    public R<CaptchaResponseVo> getCaptcha(){
        return commonService.getCaptcha();
    }

    /**
     * 七牛云上传令牌获取
     */
    @Operation(summary = "七牛云上传令牌获取")
    @GetMapping("/qiniu/upload")
    public R<String> getQiniuUploadToken(){
        return commonService.getQiniuUploadToken();
    }
}
