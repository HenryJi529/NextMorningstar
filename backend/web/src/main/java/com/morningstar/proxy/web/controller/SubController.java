package com.morningstar.proxy.web.controller;

import com.morningstar.constant.R;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.proxy.pojo.po.Sub;
import com.morningstar.proxy.pojo.vo.req.CreateSubRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateSubRequestVo;
import com.morningstar.proxy.service.SubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "代理订阅相关接口定义")
@RestController
@RequestMapping("/proxy/sub")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@PreAuthorize("hasAuthority('proxy:sub:manage')")
public class SubController {
    private final SubService subService;

    @Operation(summary = "订阅创建")
    @PostMapping("")
    public R<Sub> create(@Valid @RequestBody CreateSubRequestVo vo) {
        return R.ok(subService.create(vo));
    }

    @Operation(summary = "所有订阅获取")
    @GetMapping("")
    public R<List<Sub>> getAll() {
        return R.ok(subService.getAll());
    }

    @Operation(summary = "订阅获取")
    @GetMapping("/{id}")
    public R<Sub> get(@PathVariable("id") Integer id) {
        return R.ok(subService.get(id));
    }

    @Operation(summary = "订阅更新")
    @PatchMapping("/{id}")
    public R<Sub> update(@PathVariable("id") Integer id, @Valid @RequestBody UpdateSubRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        return R.ok(subService.update(vo));
    }

    @Operation(summary = "订阅删除")
    @DeleteMapping("/{id}")
    public R<Object> delete(@PathVariable("id") Integer id) {
        subService.delete(id);
        return R.ok();
    }
}
