package com.morningstar.pic.web.controller;

import com.morningstar.exception.BaseException;
import com.morningstar.response.PageResult;
import com.morningstar.response.R;
import com.morningstar.response.ResponseCode;
import com.morningstar.pic.lib.ImageLinkGenerators;
import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.ImageDetail;
import com.morningstar.pic.pojo.bo.Usage;
import com.morningstar.pic.pojo.po.Config;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.pic.service.ResourceService;
import com.morningstar.util.AuthUtil;
import com.morningstar.util.ImageUtil;
import com.morningstar.validation.constraint.FileNotEmpty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.util.Objects;

@Tag(name = "图床资源相关接口定义")
@RestController
@RequestMapping("/pic/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;
    private final ConfigService configService;

    @Operation(summary = "获取图片")
    @GetMapping("/image")
    public R<PageResult<Image>> getImages(
            @RequestParam("pageNum") @Positive(message = "pageNum必须大于0") int pageNum,
            @RequestParam("pageSize") @Positive(message = "pageSize必须大于0") int pageSize,
            @RequestParam("startDate") @Past(message = "startDate必须是过去的日期") @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate startLocalDate, // NOTE: 如果失败，会自动回退到 ISO-8601 格式
            @RequestParam("endDate") @PastOrPresent(message = "endDate必须是过去或当前的日期") @DateTimeFormat(pattern = "yyyy/MM/dd") LocalDate endLocalDate
    ) {
        if (startLocalDate.isAfter(endLocalDate)) {
            throw new BaseException(String.format("开始日期[%s]在结束日期[%s]后", startLocalDate, endLocalDate));
        }
        Config config = configService.getConfig(AuthUtil.getUserId());
        if (config == null) {
            throw new BaseException(ResponseCode.PIC_GITHUB_PAT_NOT_FOUND);
        }
        return R.ok(resourceService.getImages(config, pageNum, pageSize, startLocalDate, endLocalDate));
    }

    @Operation(summary = "上传大图")
    @PostMapping("/image/large")
    public R<Image> uploadLargeImage(@RequestParam("file") @FileNotEmpty() MultipartFile file) {
        Config config = configService.getConfig(AuthUtil.getUserId());
        if (config == null) {
            throw new BaseException(ResponseCode.PIC_GITHUB_PAT_NOT_FOUND);
        }
        if (resourceService.isUsageExceeded(config.getUserId())) {
            throw new BaseException(ResponseCode.PIC_TOTAL_DAY_QUOTA_EXCEEDED);
        }
        validateMultipartFileAsImage(file);
        return R.ok(resourceService.uploadLargeImage(file, config));
    }

    @Operation(summary = "上传小图")
    @PostMapping("/image/small")
    public R<Image> uploadSmallImage(@Valid @RequestBody UploadSmallImageRequestVo vo) {
        Config config = configService.getConfig(AuthUtil.getUserId());
        if (config == null) {
            throw new BaseException(ResponseCode.PIC_GITHUB_PAT_NOT_FOUND);
        }
        if (resourceService.isUsageExceeded(config.getUserId())) {
            throw new BaseException(ResponseCode.PIC_TOTAL_DAY_QUOTA_EXCEEDED);
        }
        if (!ImageUtil.isValidImageFilename(vo.getFilename())) {
            throw new BaseException(ResponseCode.PIC_IMAGE_INVALID_FILENAME, vo.getFilename());
        }
        return R.ok(resourceService.uploadSmallImage(vo, config));
    }

    @Operation(summary = "上传用于MWeb")
    @PostMapping("/mweb")
    public R<ImageDetail> uploadForMWeb(
            @RequestParam("file") @FileNotEmpty MultipartFile file,
            @RequestParam("secretKey") String secretKey,
            @RequestParam(value = "compressionQuality", required = false) Float compressionQuality,
            @RequestParam(value = "linkType", defaultValue = "JsDelivr", required = false) ImageLinkGenerators linkType
    ) {
        Config config = configService.getConfig(secretKey);
        if (config == null) {
            throw new BaseException(ResponseCode.PIC_SECRET_KEY_NOT_FOUND, secretKey);
        }
        if (compressionQuality != null) {
            if (compressionQuality <= 0.0 || compressionQuality > 1.0) {
                throw new BaseException(ResponseCode.PIC_INVALID_COMPRESSION_QUALITY, compressionQuality);
            }
            if (Math.abs(compressionQuality - 1.0) < 0.0001) {
                compressionQuality = null;
            }
            config.setCompressionQuality(compressionQuality);
        }
        if (resourceService.isUsageExceeded(config.getUserId())) {
            throw new BaseException(ResponseCode.PIC_TOTAL_DAY_QUOTA_EXCEEDED);
        }
        validateMultipartFileAsImage(file);
        return R.ok(resourceService.uploadForMWeb(file, config, linkType.getImageLinkGenerator()));
    }

    @Operation(summary = "删除图片")
    @DeleteMapping("/image")
    public R<Object> deleteImage(@RequestParam("path") String path) {
        Config config = configService.getConfig(AuthUtil.getUserId());
        if (config == null) {
            throw new BaseException(ResponseCode.PIC_GITHUB_PAT_NOT_FOUND);
        }
        resourceService.deleteImage(path, config);
        return R.ok();
    }

    @Operation(summary = "中继下载")
    @GetMapping("/relay/{ownerName}/{dirName}/{fileName}")
    public ResponseEntity<StreamingResponseBody> mirrorDownload(@PathVariable String ownerName, @PathVariable String dirName, @PathVariable String fileName) {
        return resourceService.relayDownload(ownerName, dirName, fileName);
    }

    @Operation(summary = "获取资源使用情况")
    @GetMapping("/usage")
    public R<Usage> getUsage() {
        Config config = configService.getConfig(AuthUtil.getUserId());
        if (config == null) {
            throw new BaseException(ResponseCode.PIC_GITHUB_PAT_NOT_FOUND);
        }
        return R.ok(resourceService.getUsage(config));
    }

    private void validateMultipartFileAsImage(MultipartFile file) {
        if (!ImageUtil.isValidImageFilename(file.getOriginalFilename())) {
            throw new BaseException(ResponseCode.PIC_IMAGE_INVALID_FILENAME, file.getOriginalFilename());
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new BaseException(ResponseCode.PIC_IMAGE_INVALID_CONTENT_TYPE, file.getOriginalFilename(), file.getContentType());
        }
    }
}
