package com.morningstar.pic.web.controller;

import com.morningstar.constant.R;
import com.morningstar.exception.PicGithubPatUnsetException;
import com.morningstar.exception.PicImageInvalidContentTypeException;
import com.morningstar.exception.PicImageInvalidFilenameException;
import com.morningstar.exception.PicUsageExceededException;
import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.Usage;
import com.morningstar.pic.pojo.po.Config;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.pic.service.ResourceService;
import com.morningstar.util.AuthUtil;
import com.morningstar.util.ImageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Objects;

@Tag(name = "图床资源相关接口定义")
@RestController
@RequestMapping("/pic/resource")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceController {
    private final ResourceService resourceService;
    private final ConfigService configService;

    @Operation(summary = "获取所有的图片")
    @GetMapping("/image")
    public R<List<Image>> getAllImages(){
        Config config = configService.getConfig(AuthUtil.getUserId());
        if(config == null){
            throw new PicGithubPatUnsetException();
        }
        return R.ok(resourceService.getImages(config));
    }

    @Operation(summary = "上传大图")
    @PostMapping("/image/large")
    public R<Image> uploadLargeImage(@RequestParam("file") MultipartFile file){
        Config config = configService.getConfig(AuthUtil.getUserId());
        if(config == null){
            throw new PicGithubPatUnsetException();
        }
        if(resourceService.isUsageExceeded(config.getUserId())){
            throw new PicUsageExceededException();
        }
        validateMultipartFileAsImage(file);
        return R.ok(resourceService.uploadLargeImage(file, config));
    }

    @Operation(summary = "上传小图")
    @PostMapping("/image/small")
    public R<Image> uploadSmallImage(@Valid @RequestBody UploadSmallImageRequestVo vo){
        Config config = configService.getConfig(AuthUtil.getUserId());
        if(config == null){
            throw new PicGithubPatUnsetException();
        }
        if(resourceService.isUsageExceeded(config.getUserId())){
            throw new PicUsageExceededException();
        }
        if(!ImageUtil.isValidImageFilename(vo.getFilename())){
            throw new PicImageInvalidFilenameException(vo.getFilename());
        }
        return R.ok(resourceService.uploadSmallImage(vo, config));
    }

    @Operation(summary = "上传用于MWeb")
    @PostMapping("/mweb")
    public R<Image> uploadForMWeb(@RequestParam("file") MultipartFile file, @RequestParam("secretKey") String secretKey){
        Config config = configService.getConfig(secretKey);
        if(config == null){
            throw new PicGithubPatUnsetException();
        }
        if(resourceService.isUsageExceeded(config.getUserId())){
            throw new PicUsageExceededException();
        }
        validateMultipartFileAsImage(file);
        return R.ok(resourceService.uploadForMWeb(file, config));
    }

    @Operation(summary = "删除图片")
    @DeleteMapping("/image")
    public R<Object> deleteImage(@RequestParam("path") String path){
        Config config = configService.getConfig(AuthUtil.getUserId());
        if(config == null){
            throw new PicGithubPatUnsetException();
        }
        resourceService.deleteImage(path, config);
        return R.ok();
    }

    @Operation(summary = "中继下载")
    @GetMapping("/relay/{ownerName}/{dirName}/{fileName}")
    public ResponseEntity<StreamingResponseBody> mirrorDownload(@PathVariable String ownerName, @PathVariable String dirName, @PathVariable String fileName){
        return resourceService.relayDownload(ownerName, dirName, fileName);
    }

    @Operation(summary = "获取资源使用情况")
    @GetMapping("/usage")
    public R<Usage> getUsage(){
        Config config = configService.getConfig(AuthUtil.getUserId());
        if(config == null){
            throw new PicGithubPatUnsetException();
        }
        return R.ok(resourceService.getUsage(config));
    }

    private void validateMultipartFileAsImage(MultipartFile file){
        if(!ImageUtil.isValidImageFilename(file.getOriginalFilename())){
            throw new PicImageInvalidFilenameException(file.getOriginalFilename());
        }
        if(!Objects.requireNonNull(file.getContentType()).startsWith("image")){
            throw new PicImageInvalidContentTypeException(file.getOriginalFilename(), file.getContentType());
        }
    }
}
