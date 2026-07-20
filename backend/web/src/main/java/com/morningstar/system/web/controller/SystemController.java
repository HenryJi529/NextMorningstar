package com.morningstar.system.web.controller;

import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.R;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.system.pojo.po.SysParam;
import com.morningstar.system.pojo.vo.req.CreateSysParamRequestVo;
import com.morningstar.system.pojo.vo.req.UpdateSysParamRequestVo;
import com.morningstar.system.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统相关接口定义")
@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @Operation(summary = "获取全部系统参数")
    @GetMapping("/param")
    @PreAuthorize("hasAuthority('sys:param:manage')")
    public R<List<SysParam>> getAllSysParam() {
        return R.ok(systemService.getAllSysParam());
    }

    @Operation(summary = "通过name获取指定系统参数")
    @GetMapping("/param/search")
    public R<SysParam> getSysParamByName(@RequestParam(value = "name") String paramName) {
        return R.ok(systemService.getSysParamByName(paramName));
    }

    @Operation(summary = "通过id获取指定系统参数")
    @GetMapping("/param/{id}")
    @PreAuthorize("hasAuthority('sys:param:manage')")
    public R<SysParam> getSysParamById(@PathVariable Long id) {
        return R.ok(systemService.getSysParamById(id));
    }


    @Operation(summary = "创建系统参数")
    @PostMapping("/param")
    @PreAuthorize("hasAuthority('sys:param:manage')")
    public R<Long> createSysParam(@Valid @RequestBody CreateSysParamRequestVo vo) {
        return R.ok(systemService.createSysParam(vo));
    }

    @Operation(summary = "更新系统参数")
    @PatchMapping("/param/{id}")
    @PreAuthorize("hasAuthority('sys:param:manage')")
    public R<Object> updateSysParam(@PathVariable Long id, @Valid @RequestBody UpdateSysParamRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        systemService.updateSysParam(vo);
        return R.ok();
    }

    @Operation(summary = "删除系统参数")
    @DeleteMapping("/param/{id}")
    @PreAuthorize("hasAuthority('sys:param:manage')")
    public R<Object> deleteSysParam(@PathVariable Long id) {
        systemService.deleteSysParam(id);
        return R.ok();
    }
}
