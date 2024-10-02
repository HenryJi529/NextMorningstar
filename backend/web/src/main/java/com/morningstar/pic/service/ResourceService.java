package com.morningstar.pic.service;

import com.morningstar.constant.PageResult;
import com.morningstar.pic.lib.ImageLinkGenerator;
import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.ImageDetail;
import com.morningstar.pic.pojo.bo.Usage;
import com.morningstar.pic.pojo.po.Config;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.util.UUID;


public interface ResourceService {
    PageResult<Image> getImages(Config config, int pageNum, int pageSize, LocalDate startLocalDate, LocalDate endLocalDate);

    Image uploadLargeImage(MultipartFile file, Config config);

    Image uploadSmallImage(UploadSmallImageRequestVo vo, Config config);

    ImageDetail uploadForMWeb(MultipartFile file, Config config, ImageLinkGenerator ImageLinkGenerator);

    void deleteImage(String path, Config config);

    ResponseEntity<StreamingResponseBody> relayDownload(String ownerName, String dirName, String fileName);

    Usage getUsage(Config config);

    boolean isUsageExceeded(UUID userId);
}
