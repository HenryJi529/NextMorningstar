package com.morningstar.proxy.web.controller;

import com.morningstar.response.R;
import com.morningstar.response.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.proxy.pojo.po.Sub;
import com.morningstar.proxy.pojo.vo.req.CreateSubRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateSubRequestVo;
import com.morningstar.proxy.service.SubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "代理订阅相关接口定义")
@RestController
@RequestMapping("/proxy/sub")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('proxy:sub:manage')")
public class SubController {
    private final SubService subService;

    @Operation(summary = "创建订阅")
    @PostMapping("")
    public R<Sub> create(@Valid @RequestBody CreateSubRequestVo vo) {
        return R.ok(subService.create(vo));
    }

    @Operation(summary = "获取所有订阅")
    @GetMapping("")
    public R<List<Sub>> getAll() {
        return R.ok(subService.getAll());
    }

    @Operation(summary = "获取指定订阅")
    @GetMapping("/{id}")
    public R<Sub> get(@PathVariable Integer id) {
        return R.ok(subService.get(id));
    }

    @Operation(summary = "更新订阅")
    @PatchMapping("/{id}")
    public R<Sub> update(@PathVariable Integer id, @Valid @RequestBody UpdateSubRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        return R.ok(subService.update(vo));
    }

    @Operation(summary = "删除订阅")
    @DeleteMapping("/{id}")
    public R<Object> delete(@PathVariable Integer id) {
        subService.delete(id);
        return R.ok();
    }
}
