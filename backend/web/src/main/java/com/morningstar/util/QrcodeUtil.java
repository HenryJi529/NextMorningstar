package com.morningstar.util;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.morningstar.properties.QrcodeProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QrcodeUtil {
    private final QrcodeProperties qrcodeProperties;

    private Color getColorByName(String colorName) {
        try {
            return (Color) Color.class.getField(colorName).get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    private QrConfig getQrConfig() {
        QrConfig config = new QrConfig(qrcodeProperties.getSize(), qrcodeProperties.getSize());

        config.setMargin(qrcodeProperties.getMargin());
        config.setForeColor(getColorByName(qrcodeProperties.getForeColor()));
        config.setBackColor(getColorByName(qrcodeProperties.getBackColor()));
        config.setErrorCorrection(ErrorCorrectionLevel.valueOf(qrcodeProperties.getErrorCorrectionLevel()));

        config.setImg(ImageUtil.read(new ClassPathResource("static/logo.png")));
        config.setRatio(qrcodeProperties.getLogoRatio());

        return config;
    }

    public String generateAsBase64(String data) {
        BufferedImage image = QrCodeUtil.generate(data, getQrConfig());
        return ImageUtil.getBase64StringFromBufferedImage(image);
    }
}
