package com.morningstar.common.web.controller;


import com.morningstar.common.pojo.vo.resp.ImageCaptchaResponseVo;
import com.morningstar.constant.R;
import com.morningstar.common.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通用功能相关接口定义")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonController {
    private final CommonService commonService;

    /**
     * 图形验证码获取
     */
    @Operation(summary = "图形验证码获取")
    @GetMapping("/captcha/image")
    public R<ImageCaptchaResponseVo> getImageCaptcha(){
        return commonService.getImageCaptcha();
    }

    /**
     * 邮件验证码获取
     */
    @Operation(summary = "邮件验证码获取")
    @GetMapping("/captcha/email")
    public R<Object> getEmailCaptcha(@RequestParam("email") String email){
        return commonService.getEmailCaptcha(email);
    }

    /**
     * 七牛云上传令牌获取
     */
    @Operation(summary = "七牛云上传令牌获取")
    @GetMapping("/qiniu/upload")
    public R<String> getQiniuUploadToken(){
        return commonService.getQiniuUploadToken();
    }

    /**
     * 二维码获取
     */
    @Operation(summary = "二维码获取")
    @GetMapping("/qrcode")
    public R<String> getQrcode(@RequestParam("data") String data){
        return commonService.getQrcode(data);
    }
}
