package com.morningstar.proxy.web.controller;

import com.morningstar.constant.R;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.proxy.pojo.bo.NodeDetail;
import com.morningstar.proxy.pojo.vo.req.CreateNodeRequestVo;
import com.morningstar.proxy.pojo.vo.req.UpdateNodeRequestVo;
import com.morningstar.proxy.service.NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "代理节点相关接口定义")
@RestController
@RequestMapping("/proxy/node")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@PreAuthorize("hasAuthority('proxy:node:manage')")
public class NodeController {
    private final NodeService nodeService;

    @Operation(summary = "节点创建")
    @PostMapping("")
    public R<NodeDetail> create(@Valid @RequestBody CreateNodeRequestVo vo) {
        return R.ok(nodeService.create(vo));
    }

    @Operation(summary = "所有节点获取")
    @GetMapping("")
    public R<List<NodeDetail>> getAll() {
        return R.ok(nodeService.getAll());
    }

    @Operation(summary = "节点获取")
    @GetMapping("/{id}")
    public R<NodeDetail> get(@PathVariable("id") Integer id) {
        return R.ok(nodeService.get(id));
    }

    @Operation(summary = "节点更新")
    @PatchMapping("/{id}")
    public R<NodeDetail> update(@PathVariable("id") Integer id, @Valid @RequestBody UpdateNodeRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        return R.ok(nodeService.update(vo));
    }

    @Operation(summary = "节点删除")
    @DeleteMapping("/{id}")
    public R<Object> delete(@PathVariable("id") Integer id) {
        nodeService.delete(id);
        return R.ok();
    }
}
