package com.morningstar.pic.web.controller;

import com.morningstar.constant.R;
import com.morningstar.exception.PicGithubPatUnsetException;
import com.morningstar.exception.PicUsageExceededException;
import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.bo.Usage;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.pic.service.ResourceService;
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
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        return R.ok(resourceService.getImages());
    }

    @Operation(summary = "上传大图")
    @PostMapping("/image/large")
    public R<String> uploadLargeImage(@RequestParam("file") MultipartFile file){
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        if(resourceService.isUsageExceeded()){
            throw new PicUsageExceededException();
        }
        return R.ok(resourceService.uploadLargeImage(file));
    }

    @Operation(summary = "上传小图")
    @PostMapping("/image/small")
    public R<String> uploadSmallImage(@Valid @RequestBody UploadSmallImageRequestVo vo){
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        if(resourceService.isUsageExceeded()){
            throw new PicUsageExceededException();
        }
        return R.ok(resourceService.uploadSmallImage(vo));
    }

    @Operation(summary = "删除图片")
    @DeleteMapping("/image")
    public R<Object> deleteImage(@RequestParam("path") String path){
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        resourceService.deleteImage(path);
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
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        return R.ok(resourceService.getUsage());
    }
}
