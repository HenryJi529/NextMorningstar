package com.morningstar.common.web.controller;


import com.morningstar.common.pojo.vo.resp.ImageCaptchaResponseVo;
import com.morningstar.common.service.CommonService;
import com.morningstar.constant.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public R<ImageCaptchaResponseVo> getImageCaptcha() {
        return R.ok(commonService.getImageCaptcha());
    }

    /**
     * 邮件验证码获取
     */
    @Operation(summary = "邮件验证码获取")
    @GetMapping("/captcha/email")
    public R<Object> getEmailCaptcha(@RequestParam("email") String email) {
        commonService.getEmailCaptcha(email);
        return R.ok();
    }

    /**
     * 七牛云上传令牌获取
     */
    @Operation(summary = "七牛云上传令牌获取")
    @GetMapping("/qiniu/upload")
    public R<String> getQiniuUploadToken() {
        return R.ok(commonService.getQiniuUploadToken());
    }

    /**
     * 二维码获取
     */
    @Operation(summary = "二维码获取")
    @GetMapping("/qrcode")
    public R<String> getQrcode(@Valid @NotBlank(message = "数据不能为空") @RequestParam("data") String data) {
        return R.ok(commonService.getQrcode(data));
    }
}
