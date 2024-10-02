package com.morningstar.pic.service;

import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.Usage;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;


public interface ResourceService {
    List<Image> getImages();

    String uploadLargeImage(MultipartFile file);

    String uploadSmallImage(UploadSmallImageRequestVo vo);

    void deleteImage(String path);

    ResponseEntity<StreamingResponseBody> relayDownload(String ownerName, String dirName, String fileName);

    boolean isUsageExceeded();

    Usage getUsage();
}
