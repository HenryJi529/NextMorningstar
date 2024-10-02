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
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.pojo.po.User;
import com.morningstar.constant.PageResult;
import com.morningstar.exception.BaseException;
import com.morningstar.exception.BlogArticleNotFoundException;
import com.morningstar.exception.BlogCommentNotFoundException;
import com.morningstar.properties.SiteProperties;
import com.morningstar.util.AuthUtil;
import com.morningstar.util.EmailUtil;
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
    private final UserMapper userMapper;
    private final EmailUtil emailUtil;

    private final SiteProperties siteProperties;

    private String getBaseUrl() {
        return "https://" + siteProperties.getDomain();
    }

    @Override
    public PageResult<CommentDetail> getCommentDetailByArticleId(long articleId, int pageNum, int pageSize) {
        List<CommentDetail> commentDetailList = commentMapper.selectCommentDetailByArticleId(articleId, pageSize, pageSize * (pageNum - 1));

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId);
        long totalRecordNum = commentMapper.selectCount(wrapper);

        return new PageResult<>(commentDetailList, pageNum, pageSize, totalRecordNum);
    }

    private void checkCommentExists(Long commentId) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getId, commentId);
        if (!commentMapper.exists(commentLambdaQueryWrapper)) {
            throw new BlogCommentNotFoundException(commentId);
        }
    }

    private void checkArticleExists(Long articleId) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.eq(Article::getId, articleId);
        if (!articleMapper.exists(articleLambdaQueryWrapper)) {
            throw new BlogArticleNotFoundException(articleId);
        }
    }

    private void notifyRepliedUser(Comment newComment) {
        if (newComment.getParentId() == null) {
            return;
        }

        Article article = articleMapper.selectById(newComment.getArticleId());
        User replingUser = userMapper.selectById(newComment.getUserId());
        Comment parentComment = commentMapper.selectById(newComment.getParentId());
        User repliedUser = userMapper.selectById(parentComment.getUserId());
        String link = getBaseUrl() + String.format("/blog/%s#comment-%s", article.getId(), newComment.getId());

        if (repliedUser.getEmail() != null) {
            String divider = "-".repeat(80) + "\n";
            emailUtil.sendSimpleEmailCcAdmin(
                    repliedUser.getEmail(),
                    String.format("收到回复 - 文章《%s》", article.getTitle()),
                    String.format("链接: %s\n" + divider + "用户: %s\n" + divider + "引用: \n%s\n" + divider + "回复: \n%s\n", link, replingUser.getNickname(), parentComment.getContent(), newComment.getContent())
            );
        }
    }

    private void notifyAdmin(Comment newComment) {
        Article article = articleMapper.selectById(newComment.getArticleId());
        User commentUser = userMapper.selectById(newComment.getUserId());
        String link = getBaseUrl() + String.format("/blog/%s#comment-%s", article.getId(), newComment.getId());

        String divider = "-".repeat(80) + "\n";

        emailUtil.sendSimpleEmailToAdmin(
                String.format("收到评论 - 文章《%s》", article.getTitle()),
                String.format("链接: %s\n" + divider + "用户: %s\n" + divider + "评论: \n%s\n", link, commentUser.getNickname(), newComment.getContent())
        );

    }

    @Override
    public void createComment(CreateCommentRequestVo vo) {
        if (vo.getParentId() != null) {
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

        notifyAdmin(comment);
        notifyRepliedUser(comment);
    }

    private LambdaQueryWrapper<CommentUser> getThumbsWrapperById(Long commentId, UUID userId) {
        LambdaQueryWrapper<CommentUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentUser::getCommentId, commentId);
        wrapper.eq(CommentUser::getUserId, userId);
        return wrapper;
    }

    @Override
    public void thumbsUp(long commentId) {
        UUID userId = AuthUtil.getUserId();
        LambdaQueryWrapper<CommentUser> wrapper = getThumbsWrapperById(commentId, userId);
        if (commentUserMapper.exists(wrapper)) {
            commentUserMapper.delete(wrapper);
        }
        int result = commentUserMapper.insert(
                CommentUser.builder().commentId(commentId).userId(userId).isApproved(true).build()
        );
        if (result != 1) {
            throw new BaseException("点赞(支持)失败");
        }
    }

    @Override
    public void thumbsDown(long commentId) {
        UUID userId = AuthUtil.getUserId();
        LambdaQueryWrapper<CommentUser> wrapper = getThumbsWrapperById(commentId, userId);
        if (commentUserMapper.exists(wrapper)) {
            commentUserMapper.delete(wrapper);
        }
        int result = commentUserMapper.insert(
                CommentUser.builder().commentId(commentId).userId(userId).isApproved(false).build()
        );
        if (result != 1) {
            throw new BaseException("点踩(反对)失败");
        }
    }

    @Override
    public void thumbsNone(long commentId) {
        UUID userId = AuthUtil.getUserId();
        LambdaQueryWrapper<CommentUser> wrapper = getThumbsWrapperById(commentId, userId);
        int result = commentUserMapper.delete(wrapper);
        if (result != 1) {
            throw new BaseException("清除评价(中立)失败");
        }
    }
}
