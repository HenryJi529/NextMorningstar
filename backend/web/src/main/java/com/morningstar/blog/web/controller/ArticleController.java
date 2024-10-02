package com.morningstar.blog.web.controller;

import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.vo.req.CreateArticleRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateArticleRequestVo;
import com.morningstar.blog.service.ArticleService;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;
import com.morningstar.constant.ResponseCode;
import com.morningstar.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "博客文章相关接口定义")
@RestController
@RequestMapping("/blog/article")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleController {
    private final ArticleService articleService;

    @Operation(summary = "获取文章(根据id)")
    @GetMapping("/detail/{id}")
    public R<ArticleDetail> getArticleDetailByArticleId(@PathVariable("id") Long id) {
        return R.ok(articleService.getArticleDetailByArticleId(id));
    }

    @Operation(summary = "获取文章(全部)")
    @GetMapping
    public R<PageResult<ArticleDetail>> getAllArticleDetail(
            @RequestParam("pageNum") @Positive(message = "pageNum必须大于0") int pageNum,
            @RequestParam("pageSize") @Positive(message = "pageSize必须大于0") int pageSize
    ) {
        return R.ok(articleService.getAllArticleDetail(pageNum, pageSize));
    }

    @Operation(summary = "获取文章(根据词条)")
    @GetMapping("/search")
    public R<PageResult<ArticleDetail>> getArticleDetailByTerm(
            @RequestParam("term") String term,
            @RequestParam("pageNum") @Positive(message = "pageNum必须大于0") int pageNum,
            @RequestParam("pageSize") @Positive(message = "pageSize必须大于0") int pageSize
    ) {
        return R.ok(articleService.getArticleDetailByTerm(term.toLowerCase(), pageNum, pageSize));
    }

    @Operation(summary = "检索库文章刷新")
    @PreAuthorize("hasAuthority('blog:article:manage')")
    @GetMapping("/search/refresh")
    public R<Object> refreshArticleIndex() {
        articleService.refreshArticleIndex();
        return R.ok();
    }

    @Operation(summary = "获取文章(根据分类)")
    @GetMapping("/category/{id}")
    public R<PageResult<ArticleDetail>> getArticleDetailByCategoryId(
            @PathVariable("id") int id,
            @RequestParam("pageNum") @Positive(message = "pageNum必须大于0") int pageNum,
            @RequestParam("pageSize") @Positive(message = "pageSize必须大于0") int pageSize) {
        return R.ok(articleService.getArticleDetailByCategoryId(id, pageNum, pageSize));
    }

    @Operation(summary = "获取文章(根据标签)")
    @GetMapping("/tag/{id}")
    public R<PageResult<ArticleDetail>> getArticleDetailByTagId(
            @PathVariable("id") int id,
            @RequestParam("pageNum") @Positive(message = "pageNum必须大于0") int pageNum,
            @RequestParam("pageSize") @Positive(message = "pageSize必须大于0") int pageSize) {
        return R.ok(articleService.getArticleDetailByTagId(id, pageNum, pageSize));
    }

    @Operation(summary = "随机获取文章id")
    @GetMapping("/random")
    public R<String> getRandomArticleId() {
        return R.ok(articleService.getRandomArticleId());
    }

    @Operation(summary = "创建文章")
    @PostMapping
    @PreAuthorize("hasAuthority('blog:article:manage')")
    public R<String> createArticle(@Valid @RequestBody CreateArticleRequestVo vo) {
        return R.ok(articleService.createArticle(vo));
    }

    @Operation(summary = "更新文章(根据id)")
    @PatchMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('blog:article:manage')")
    public R<Object> updateArticle(@PathVariable("id") Long id, @Valid @RequestBody UpdateArticleRequestVo vo) {
        if (!vo.getId().equals(id)) {
            throw new BaseException(ResponseCode.ID_MISMATCH);
        }
        articleService.updateArticle(vo);
        return R.ok();
    }

    @Operation(summary = "删除文章(根据id)")
    @DeleteMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('blog:article:manage')")
    public R<Object> deleteArticle(@PathVariable("id") Long id) {
        articleService.deleteArticle(id);
        return R.ok();
    }
}
