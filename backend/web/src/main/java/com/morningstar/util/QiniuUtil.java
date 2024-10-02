package com.morningstar.util;

import cn.hutool.json.JSONUtil;
import com.morningstar.properties.QiniuProperties;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QiniuUtil {
    private final QiniuProperties qiniuProperties;

    public Auth auth() {
        return Auth.create(qiniuProperties.getAccessKey(), qiniuProperties.getSecretKey());
    }

    public String getUploadToken() {
        return auth().uploadToken(qiniuProperties.getBucket());
    }

    public String getDownloadUrl(String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return String.format("%s/%s", qiniuProperties.getDomain(), encodedFileName);
    }

    public boolean uploadFile(String fileName, byte[] content) {
        String uploadToken = getUploadToken();
        UploadManager uploadManager = new UploadManager(new Configuration(Region.xinjiapo()));
        try {
            Response response = uploadManager.put(content, fileName, uploadToken);
            //解析上传成功的结果
            DefaultPutRet putRet = JSONUtil.toBean(response.bodyString(), DefaultPutRet.class);
            return putRet.key.equals(fileName);
        } catch (QiniuException e) {
            return false;
        }
    }
}
