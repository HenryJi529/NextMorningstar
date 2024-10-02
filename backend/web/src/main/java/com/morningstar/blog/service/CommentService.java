package com.morningstar.blog.service;

import com.morningstar.blog.pojo.bo.CommentDetail;
import com.morningstar.blog.pojo.vo.req.CreateCommentRequestVo;
import com.morningstar.constant.PageResult;

public interface CommentService {
    PageResult<CommentDetail> getCommentDetailByArticleId(long articleId, int pageNum, int pageSize);

    void createComment(CreateCommentRequestVo vo);

    void thumbsUp(long commentId);

    void thumbsDown(long commentId);

    void thumbsNone(long commentId);
}
