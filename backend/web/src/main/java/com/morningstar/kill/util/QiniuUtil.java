package com.morningstar.kill.util;

import com.morningstar.kill.properties.QiniuProperties;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class QiniuUtil {
    @Autowired
    private QiniuProperties qiniuProperties;

    public Auth auth(){
        return Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
    }

    public String getUploadToken(){
        return auth().uploadToken(qiniuProperties.getBucket());
    }

    public String getDownloadUrl(String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return String.format("%s/%s", qiniuProperties.getDomain(), encodedFileName);
    }
}
