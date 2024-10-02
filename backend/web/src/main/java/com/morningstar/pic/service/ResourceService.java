package com.morningstar.pic.service;

import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.Usage;
import com.morningstar.pic.pojo.po.Config;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.UUID;


public interface ResourceService {
    List<Image> getImages(Config config);

    Image uploadLargeImage(MultipartFile file, Config config);

    Image uploadSmallImage(UploadSmallImageRequestVo vo, Config config);

    Image uploadForMWeb(MultipartFile file, Config config);

    void deleteImage(String path, Config config);

    ResponseEntity<StreamingResponseBody> relayDownload(String ownerName, String dirName, String fileName);

    Usage getUsage(Config config);

    boolean isUsageExceeded(UUID userId);
}
