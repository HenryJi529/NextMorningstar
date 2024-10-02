package com.morningstar.pic.web.controller;

import com.morningstar.constant.R;
import com.morningstar.exception.PicInvalidCompressionQualityException;
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
    @GetMapping("/github-pat")
    public R<String> getGithubPat() {
        String pat = configService.getGithubPat(AuthUtil.getUserId());
        if (pat != null) {
            configService.validateGithubPat(pat);
        }
        return R.ok(pat);
    }

    @Operation(summary = "设置GithubPat")
    @PostMapping("/github-pat")
    public R<Object> setGithubPat(@RequestBody @Valid SetGithubPatRequestVo vo) {
        configService.setGithubPat(vo.getPat(), AuthUtil.getUserId());
        return R.ok();
    }

    @Operation(summary = "删除GithubPat")
    @DeleteMapping("/github-pat")
    public R<Object> deleteGithubPat() {
        configService.deleteGithubPat(AuthUtil.getUserId());
        return R.ok();
    }

    @Operation(summary = "获取Github账号")
    @GetMapping("/github-account")
    public R<String> getGithubAccount() {
        return R.ok(configService.getGithubAccount(AuthUtil.getUserId()));
    }

    @Operation(summary = "获取压缩质量参数")
    @GetMapping("/compression-quality")
    public R<Float> getCompressionQuality() {
        return R.ok(configService.getCompressionQuality(AuthUtil.getUserId()));
    }

    @Operation(summary = "设置压缩质量参数")
    @PostMapping("/compression-quality")
    public R<Object> setCompressionQuality(@RequestParam("compressionQuality") Float compressionQuality) {
        if (compressionQuality <= 0.0 || compressionQuality > 1.0) {
            throw new PicInvalidCompressionQualityException(compressionQuality);
        }
        if (Math.abs(compressionQuality - 1.0) < 0.0001) {
            compressionQuality = null;
        }
        configService.setCompressionQuality(compressionQuality, AuthUtil.getUserId());
        return R.ok();
    }

    @Operation(summary = "获取Secret Key")
    @GetMapping("/secret-key")
    public R<String> getSecretKey() {
        return R.ok(configService.getSecretKey(AuthUtil.getUserId()));
    }
}
