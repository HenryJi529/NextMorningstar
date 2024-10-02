package com.morningstar.kill.web.controller;

import com.morningstar.kill.pojo.bo.LovePhotoData;
import com.morningstar.kill.service.LoveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "爱の相册相关接口定义")
@RestController
@RequestMapping("/api/love")
public class LoveController {
    @Autowired
    private LoveService loveService;

    @Operation(summary = "随机数据获取")
    @GetMapping("/random")
    public LovePhotoData getRandomData(){
        return loveService.getRandomData();
    }
}
