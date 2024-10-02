package com.morningstar.pic.web.controller;

import com.morningstar.constant.R;
import com.morningstar.exception.PicGithubPatUnsetException;
import com.morningstar.pic.pojo.bo.Image;
import com.morningstar.pic.pojo.vo.req.UploadSmallImageRequestVo;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.pic.service.ImageService;
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

@Tag(name = "图床图片相关接口定义")
@RestController
@RequestMapping("/pic/image")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageController {
    private final ImageService imageService;

    private final ConfigService configService;

    @Operation(summary = "获取所有的图片")
    @GetMapping
    public R<List<Image>> getAllImages(){
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        return R.ok(imageService.getImages());
    }

    @Operation(summary = "上传大图")
    @PostMapping("/large")
    public R<String> uploadLargeImage(@RequestParam("file") MultipartFile file){
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        return R.ok(imageService.uploadLargeImage(file));
    }

    @Operation(summary = "上传小图")
    @PostMapping("/small")
    public R<String> uploadSmallImage(@Valid @RequestBody UploadSmallImageRequestVo vo){
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        return R.ok(imageService.uploadSmallImage(vo));
    }

    @Operation(summary = "删除图片")
    @DeleteMapping
    public R<Object> deleteImage(@RequestParam("path") String path){
        if(configService.getGithubPat().isEmpty()){
            throw new PicGithubPatUnsetException();
        }
        imageService.deleteImage(path);
        return R.ok();
    }

    @Operation(summary = "中继下载")
    @GetMapping("/relay/{ownerName}/{dirName}/{fileName}")
    public ResponseEntity<StreamingResponseBody> mirrorDownload(@PathVariable String ownerName, @PathVariable String dirName, @PathVariable String fileName){
        return imageService.relayDownload(ownerName, dirName, fileName);
    }
}
