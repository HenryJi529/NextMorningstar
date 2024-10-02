package com.morningstar.pic.web.controller;

import com.morningstar.constant.R;
import com.morningstar.pic.pojo.vo.req.SetGithubPatRequestVo;
import com.morningstar.pic.service.ConfigService;
import com.morningstar.util.AuthUtil;
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
        return R.ok(configService.getGithubPat(AuthUtil.getUserId()));
    }

    @Operation(summary = "设置GithubPat")
    @PostMapping("/pat")
    public R<Object> setGithubPat(@RequestBody @Valid SetGithubPatRequestVo vo){
        configService.setGithubPat(vo.getPat(), AuthUtil.getUserId());
        return R.ok();
    }

    @Operation(summary = "删除GithubPat")
    @DeleteMapping("/pat")
    public R<Object> deleteGithubPat(){
        configService.deleteGithubPat(AuthUtil.getUserId());
        return R.ok();
    }

    @Operation(summary = "获取Github账号")
    @GetMapping("/account")
    public R<String> getGithubAccount(){
        return R.ok(configService.getGithubAccount(AuthUtil.getUserId()));
    }
}
