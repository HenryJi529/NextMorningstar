package com.morningstar.blog.web.controller;

import com.morningstar.blog.pojo.bo.TagDetail;
import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.blog.pojo.vo.req.CreateTagRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateTagRequestVo;
import com.morningstar.blog.service.TagService;
import com.morningstar.constant.R;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "博客标签相关接口定义")
@RestController
@RequestMapping("/blog/tag")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TagController {
    private final TagService tagService;

    @Operation(summary = "获取标签(全部)")
    @GetMapping
    public R<List<TagDetail>> getAllTagDetail() {
        return R.ok(tagService.getAllTagDetail());
    }

    @Operation(summary = "创建标签")
    @PostMapping
    @PreAuthorize("hasAuthority('blog:tag:manage')")
    public R<Integer> createTag(@Valid @RequestBody CreateTagRequestVo vo) {
        return R.ok(tagService.createTag(vo));
    }

    @Operation(summary = "获取标签(根据id)")
    @GetMapping("/{id}")
    public R<Tag> getTagById(@PathVariable("id") Integer id) {
        return R.ok(tagService.getTagById(id));
    }

    @Operation(summary = "删除标签(根据id)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('blog:tag:manage')")
    public R<Object> deleteTagById(@PathVariable("id") Integer id) {
        tagService.deleteTagById(id);
        return R.ok();
    }

    @Operation(summary = "更新标签(根据id)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('blog:tag:manage')")
    public R<Object> updateTagById(@PathVariable("id") Integer id, @Valid @RequestBody UpdateTagRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        tagService.updateTagById(vo);
        return R.ok();
    }
}
