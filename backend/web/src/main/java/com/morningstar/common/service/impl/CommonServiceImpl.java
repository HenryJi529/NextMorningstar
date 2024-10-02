package com.morningstar.common.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.morningstar.common.pojo.vo.resp.ImageCaptchaResponseVo;
import com.morningstar.common.service.CommonService;
import com.morningstar.constant.RedisConstant;
import com.morningstar.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonServiceImpl implements CommonService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final QiniuUtil qiniuUtil;

    private final QrcodeUtil qrcodeUtil;

    private final EmailUtil emailUtil;

    private final IdWorker idWorker;

    @Override
    public ImageCaptchaResponseVo getImageCaptcha() {
        LineCaptcha captcha;
        do {
            // 参数分别是 宽、高、验证码长度、干扰线数量
            captcha = CaptchaUtil.createLineCaptcha(250, 40, 4, 5);
            // 设置背景颜色清灰
            captcha.setBackground(Color.LIGHT_GRAY);
        } while (captcha.getCode().toUpperCase().contains("I") || captcha.getCode().toUpperCase().contains("L") || captcha.getCode().toUpperCase().contains("O") || captcha.getCode().contains("1") || captcha.getCode().contains("0"));

        log.info("生成图形验证码: {}", captcha.getCode());

        // 生成sessionId
        String sessionId = Long.toHexString(idWorker.nextId());
        // 将sessionId和验证码保存在redis下，并设置缓存中数据存活时间五分钟
        redisTemplate.opsForValue().set(RedisConstant.CHECK_PREFIX + sessionId, captcha.getCode(), 5, TimeUnit.MINUTES);

        // 组装响应数据
        return ImageCaptchaResponseVo.builder().sessionId(sessionId).imageData("data:image/png;base64," + captcha.getImageBase64()).build();
    }

    @Override
    public String getQiniuUploadToken() {
        return qiniuUtil.getUploadToken();
    }

    @Override
    public String getQrcode(String data) {
        return qrcodeUtil.generateAsBase64(data);
    }

    @Override
    public void getEmailCaptcha(String email) {
        String code = RandomUtil.getNumericString(6);
        redisTemplate.opsForValue().set(RedisConstant.CHECK_PREFIX + email, code, 5, TimeUnit.MINUTES);
        emailUtil.sendSimpleEmail(email, "邮件验证码", String.format("本次操作验证码为: %s", code));
    }
}
