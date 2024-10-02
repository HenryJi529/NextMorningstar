package com.morningstar.love.web.controller;

import com.morningstar.love.pojo.bo.LovePhotoData;
import com.morningstar.love.service.LoveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "爱の相册相关接口定义")
@RestController
@RequestMapping("/love")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LoveController {
    private final LoveService loveService;

    @Operation(summary = "随机数据获取")
    @GetMapping("/random")
    public LovePhotoData getRandomData(){
        return loveService.getRandomData();
    }
}
