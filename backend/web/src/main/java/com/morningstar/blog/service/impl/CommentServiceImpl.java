package com.morningstar.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.blog.dao.mapper.ArticleMapper;
import com.morningstar.blog.dao.mapper.CommentMapper;
import com.morningstar.blog.dao.mapper.CommentUserMapper;
import com.morningstar.blog.pojo.bo.CommentDetail;
import com.morningstar.blog.pojo.po.Article;
import com.morningstar.blog.pojo.po.Comment;
import com.morningstar.blog.pojo.po.CommentUser;
import com.morningstar.blog.pojo.vo.req.CreateCommentRequestVo;
import com.morningstar.blog.service.CommentService;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;
import com.morningstar.exception.BaseException;
import com.morningstar.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final CommentUserMapper commentUserMapper;

    @Override
    public R<PageResult<CommentDetail>> getCommentDetailByArticleId(long articleId, int pageNum, int pageSize) {
        List<CommentDetail> commentDetailList = commentMapper.selectCommentDetailByArticleId(articleId, pageSize, pageSize * (pageNum-1));

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId);
        long totalRecordNum = commentMapper.selectCount(wrapper);

        return R.ok(new PageResult<>(commentDetailList, pageNum, pageSize, totalRecordNum));
    }

    private void checkCommentExists(Long commentId) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getId, commentId);
        if(!commentMapper.exists(commentLambdaQueryWrapper)){
            throw new BaseException("评论不存在");
        }
    }

    private void checkArticleExists(Long articleId) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.eq(Article::getId, articleId);
        if(!articleMapper.exists(articleLambdaQueryWrapper)){
            throw new BaseException("文章不存在");
        }
    }

    @Override
    public R<Object> createComment(CreateCommentRequestVo vo) {
        if(vo.getParentId() != null) {
            checkCommentExists(vo.getParentId());
        }
        checkArticleExists(vo.getArticleId());

        UUID userId = AuthUtil.getUserId();
        Comment comment = Comment
                .builder()
                .parentId(vo.getParentId())
                .userId(userId)
                .articleId(vo.getArticleId())
                .content(vo.getContent())
                .build();
        commentMapper.insert(comment);

        return R.ok();
    }

    private LambdaQueryWrapper<CommentUser> getThumbsWrapperById(Long commentId, UUID userId) {
        LambdaQueryWrapper<CommentUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentUser::getCommentId, commentId);
        wrapper.eq(CommentUser::getUserId, userId);
        return wrapper;
    }

    @Override
    public R<Object> thumbsUp(long commentId) {
        UUID userId = AuthUtil.getUserId();
        LambdaQueryWrapper<CommentUser> wrapper = getThumbsWrapperById(commentId, userId);
        if(commentUserMapper.exists(wrapper)) {
            commentUserMapper.delete(wrapper);
        }
        int result = commentUserMapper.insert(
                CommentUser.builder().commentId(commentId).userId(userId).isApproved(true).build()
        );
        return result == 1 ? R.ok(): R.error();
    }

    @Override
    public R<Object> thumbsDown(long commentId) {
        UUID userId = AuthUtil.getUserId();
        LambdaQueryWrapper<CommentUser> wrapper = getThumbsWrapperById(commentId, userId);
        if(commentUserMapper.exists(wrapper)) {
            commentUserMapper.delete(wrapper);
        }
        int result = commentUserMapper.insert(
                CommentUser.builder().commentId(commentId).userId(userId).isApproved(false).build()
        );
        return result == 1 ? R.ok(): R.error();
    }

    @Override
    public R<Object> thumbsNone(long commentId) {
        UUID userId = AuthUtil.getUserId();
        LambdaQueryWrapper<CommentUser> wrapper = getThumbsWrapperById(commentId, userId);
        int result = commentUserMapper.delete(wrapper);
        return result == 1 ? R.ok(): R.error();
    }
}
