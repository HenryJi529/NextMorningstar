package com.morningstar.blog.service;

import com.morningstar.blog.pojo.bo.CommentDetail;
import com.morningstar.blog.pojo.vo.req.CreateCommentRequestVo;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;

public interface CommentService {
    R<PageResult<CommentDetail>> getCommentDetailByArticleId(long articleId, int pageNum, int pageSize);

    R<Object> createComment(CreateCommentRequestVo vo);

    R<Object> thumbsUp(long commentId);

    R<Object> thumbsDown(long commentId);

    R<Object> thumbsNone(long commentId);
}
