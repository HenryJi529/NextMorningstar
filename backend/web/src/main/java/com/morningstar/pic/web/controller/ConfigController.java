package com.morningstar.pic.web.controller;

import com.morningstar.constant.R;
import com.morningstar.pic.pojo.vo.req.SetGithubPatRequestVo;
import com.morningstar.pic.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "图床配置相关接口定义")
@RestController
@RequestMapping("/pic/config")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigController {
    private final ConfigService configService;

    @Operation(summary = "获取GithubPat")
    @GetMapping("/pat")
    public R<String> getGithubPat(){
        return configService.getGithubPat();
    }

    @Operation(summary = "设置GithubPat")
    @PostMapping("/pat")
    public R<Object> setGithubPat(@RequestBody @Valid SetGithubPatRequestVo vo){
        return configService.setGithubPat(vo.getPat());
    }

}
