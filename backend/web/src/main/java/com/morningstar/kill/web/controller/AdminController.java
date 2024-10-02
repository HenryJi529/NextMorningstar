package com.morningstar.kill.web.controller;

import com.morningstar.kill.pojo.vo.resp.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理相关接口定义")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Operation(summary = "主页信息获取")
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/")
    public R<String> home(){
        return R.ok();
    }
}
