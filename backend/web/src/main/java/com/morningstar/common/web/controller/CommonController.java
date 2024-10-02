package com.morningstar.common.web.controller;


import com.morningstar.common.pojo.vo.resp.ImageCaptchaResponseVo;
import com.morningstar.common.service.CommonService;
import com.morningstar.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "通用功能相关接口定义")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;

    @Operation(summary = "获取图形验证码")
    @GetMapping("/captcha/image")
    public R<ImageCaptchaResponseVo> getImageCaptcha() {
        return R.ok(commonService.getImageCaptcha());
    }

    @Operation(summary = "获取邮件验证码")
    @GetMapping("/captcha/email")
    public R<Object> getEmailCaptcha(@RequestParam("email") String email) {
        commonService.getEmailCaptcha(email);
        return R.ok();
    }

    @Operation(summary = "获取七牛云上传令牌")
    @GetMapping("/qiniu/upload")
    public R<String> getQiniuUploadToken() {
        return R.ok(commonService.getQiniuUploadToken());
    }

    @Operation(summary = "生成二维码")
    @GetMapping("/qrcode")
    public R<String> getQrcode(@NotBlank(message = "数据不能为空") @RequestParam("data") String data) {
        return R.ok(commonService.getQrcode(data));
    }
}
