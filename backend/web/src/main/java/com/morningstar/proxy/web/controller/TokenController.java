package com.morningstar.proxy.web.controller;

import com.morningstar.constant.R;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.proxy.pojo.po.Token;
import com.morningstar.proxy.pojo.vo.req.CreateTokenRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateTokenRequestVo;
import com.morningstar.proxy.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "代理令牌相关接口定义")
@RestController
@RequestMapping("/proxy/token")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@PreAuthorize("hasAuthority('proxy:token:manage')")
public class TokenController {
    private final TokenService tokenService;

    @Operation(summary = "令牌创建")
    @PostMapping("")
    public R<Token> create(@Valid @RequestBody CreateTokenRequestVo vo) {
        return R.ok(tokenService.create(vo));
    }

    @Operation(summary = "全部令牌获取")
    @GetMapping("")
    public R<List<Token>> getAll() {
        return R.ok(tokenService.getAll());
    }

    @Operation(summary = "令牌获取")
    @GetMapping("/{id}")
    public R<Token> get(@PathVariable("id") Integer id) {
        return R.ok(tokenService.get(id));
    }

    @Operation(summary = "令牌更新")
    @PatchMapping("/{id}")
    public R<Token> update(@PathVariable("id") Integer id, @Valid @RequestBody UpdateTokenRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        return R.ok(tokenService.update(vo));
    }

    @Operation(summary = "令牌删除")
    @DeleteMapping("/{id}")
    public R<Object> delete(@PathVariable("id") Integer id) {
        tokenService.delete(id);
        return R.ok();
    }
}