package com.morningstar.kill.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.morningstar.kill.constant.RedisConstant;
import com.morningstar.kill.pojo.vo.resp.CaptchaResponseVo;
import com.morningstar.kill.pojo.vo.resp.R;
import com.morningstar.kill.service.CommonService;
import com.morningstar.kill.util.IdWorker;
import com.morningstar.kill.util.QiniuUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("commonService")
public class CommonServiceImpl implements CommonService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private QiniuUtil qiniuUtil;

    @Autowired
    private IdWorker idWorker;

    @Override
    public R<CaptchaResponseVo> getCaptcha() {
        LineCaptcha captcha;
        do {
            // 参数分别是 宽、高、验证码长度、干扰线数量
            captcha = CaptchaUtil.createLineCaptcha(250, 40, 4, 5);
            // 设置背景颜色清灰
            captcha.setBackground(Color.LIGHT_GRAY);
        } while (captcha.getCode().toUpperCase().contains("I") || captcha.getCode().contains("1"));

        log.info("生成校验码: {}", captcha.getCode());

        // 生成sessionId
        String sessionId = Long.toHexString(idWorker.nextId());
        // 将sessionId和校验码保存在redis下，并设置缓存中数据存活时间五分钟
        redisTemplate.opsForValue().set(RedisConstant.CHECK_PREFIX +sessionId, captcha.getCode(),5, TimeUnit.MINUTES);

        // 组装响应数据
        CaptchaResponseVo captchaResponseVo = CaptchaResponseVo.builder().sessionId(sessionId).imageData("data:image/png;base64," + captcha.getImageBase64()).build();
        // 设置响应数据格式
        return R.ok(captchaResponseVo);
    }

    @Override
    public R<String> getQiniuUploadToken() {
        return R.ok(qiniuUtil.getUploadToken());
    }
}
