package com.morningstar.blog.web.controller;

import com.morningstar.blog.pojo.bo.CategoryDetail;
import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.vo.req.CreateCategoryRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateCategoryRequestVo;
import com.morningstar.blog.service.CategoryService;
import com.morningstar.constant.R;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "博客分类相关接口定义")
@RestController
@RequestMapping("/blog/category")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "获取分类(全部)")
    @GetMapping
    public R<List<CategoryDetail>> getAllCategoryDetail() {
        return R.ok(categoryService.getAllCategoryDetail());
    }

    @Operation(summary = "创建分类")
    @PostMapping
    @PreAuthorize("hasAuthority('blog:category:manage')")
    public R<Integer> createCategory(@Valid @RequestBody CreateCategoryRequestVo vo) {
        return R.ok(categoryService.createCategory(vo));
    }

    @Operation(summary = "获取分类(根据id)")
    @GetMapping("/{id}")
    public R<Category> getCategoryById(@PathVariable("id") Integer id) {
        return R.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "删除分类(根据id)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('blog:category:manage')")
    public R<Object> deleteCategoryById(@PathVariable("id") Integer id) {
        categoryService.deleteCategoryById(id);
        return R.ok();
    }

    @Operation(summary = "更新分类(根据id)")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('blog:category:manage')")
    public R<Object> updateCategoryById(@PathVariable("id") Integer id, @Valid @RequestBody UpdateCategoryRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        categoryService.updateCategoryById(vo);
        return R.ok();
    }
}
