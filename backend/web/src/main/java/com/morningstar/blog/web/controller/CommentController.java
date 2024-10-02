package com.morningstar.blog.web.controller;

import com.morningstar.blog.pojo.bo.CommentDetail;
import com.morningstar.blog.pojo.vo.req.CreateCommentRequestVo;
import com.morningstar.blog.service.CommentService;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "博客评论相关接口定义")
@RestController
@RequestMapping("/blog/comment")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "获取评论(根据文章)")
    @GetMapping("/article/{id}")
    public R<PageResult<CommentDetail>> getCommentDetailByArticleId(
            @PathVariable("id") long articleId,
            @RequestParam("pageNum") @Positive(message = "pageNum必须大于0") int pageNum,
            @RequestParam("pageSize") @Positive(message = "pageSize必须大于0") int pageSize) {
        return R.ok(commentService.getCommentDetailByArticleId(articleId, pageNum, pageSize));
    }

    @Operation(summary = "创建评论")
    @PostMapping
    public R<Object> createComment(@Valid @RequestBody CreateCommentRequestVo vo) {
        commentService.createComment(vo);
        return R.ok();
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/thumbs-up/{id}")
    public R<Object> thumbsUpComment(@PathVariable("id") long commentId) {
        commentService.thumbsUp(commentId);
        return R.ok();
    }

    @Operation(summary = "点踩评论")
    @PostMapping("/thumbs-down/{id}")
    public R<Object> thumbsDownComment(@PathVariable("id") long commentId) {
        commentService.thumbsDown(commentId);
        return R.ok();
    }

    @Operation(summary = "清除点赞/踩")
    @PostMapping("/thumbs-none/{id}")
    public R<Object> thumbsNoneComment(@PathVariable("id") long commentId) {
        commentService.thumbsNone(commentId);
        return R.ok();
    }
}
