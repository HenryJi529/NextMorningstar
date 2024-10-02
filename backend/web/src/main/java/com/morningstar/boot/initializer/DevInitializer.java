package com.morningstar.boot.initializer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.blog.dao.mapper.*;
import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.bo.ArticleDoc;
import com.morningstar.blog.pojo.po.*;
import com.morningstar.blog.util.BlogFakeUtil;
import com.morningstar.infra.constant.ElasticSearchConstant;
import com.morningstar.infra.util.EsUtil;
import com.morningstar.system.dao.mapper.UserMapper;
import com.morningstar.system.pojo.po.User;
import com.morningstar.system.util.SystemFakeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevInitializer extends CommonInitializer {

    private final BlogFakeUtil blogFakeUtil;

    private final SystemFakeUtil systemFakeUtil;

    private final UserMapper userMapper;

    private final CategoryMapper categoryMapper;

    private final TagMapper tagMapper;

    private final ArticleMapper articleMapper;

    private final ArticleTagMapper articleTagMapper;

    private final CommentMapper commentMapper;

    private final CommentUserMapper commentUserMapper;

    private final EsUtil esUtil;

    @Override
    protected void initializeAccount() {
        super.initializeAccount();
        // 添加更多用户
        long currentUserNum = userMapper.selectCount(null);
        while (currentUserNum < 100) {
            try {
                User fakerUser = systemFakeUtil.fakeUser();
                userMapper.insert(fakerUser);
                currentUserNum++;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    protected void initializeBlog() {
        super.initializeBlog();

        String indexName = ElasticSearchConstant.BLOG_ARTICLE_INDEX;

        // 生成足够多的分类
        long currentCategoryNum = categoryMapper.selectCount(null);
        while (currentCategoryNum < 6) {
            Category category = blogFakeUtil.fakeCategory();
            try {
                categoryMapper.insert(category);
                currentCategoryNum += 1;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
        // 生成足够多的标签
        long currentTagNum = tagMapper.selectCount(null);
        while (currentTagNum < 20) {
            Tag tag = blogFakeUtil.fakeTag();
            try {
                tagMapper.insert(tag);
                currentTagNum += 1;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
        // 生成足够多的文章
        long currentArticleNum = articleMapper.selectCount(null);
        while (currentArticleNum < 100) {
            ArticleDetail articleDetail = blogFakeUtil.fakeArticleDetail();
            Article article = articleDetail.getArticle();
            List<Tag> tags = articleDetail.getTags();

            try {
                // 插入数据库
                articleMapper.insert(article);
                articleTagMapper.insert(tags.stream()
                        .map(tag -> ArticleTag.builder().articleId(article.getId()).tagId(tag.getId()).build())
                        .toList());
                // 插入ElasticSearch
                esUtil.indexDocument(indexName, articleDetail.getArticle().getId().toString(), new ArticleDoc(articleDetail));

                currentArticleNum++;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

        }
        // 生成足够多的评论
        long currentCommentNum = commentMapper.selectCount(null);
        while (currentCommentNum < 1000) {
            Comment comment = blogFakeUtil.fakeComment();
            try {
                commentMapper.insert(comment);
                currentCommentNum += 1;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
        // 给评论足够多的点赞与点踩
        long currentThumbsUpOrDownNum = commentUserMapper.selectCount(null);
        while (currentThumbsUpOrDownNum < 2000) {
            User selectedUser = userMapper.selectRandomN(1).get(0);
            Comment selectedComment = commentMapper.selectRandomN(1).get(0);
            LambdaQueryWrapper<CommentUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CommentUser::getUserId, selectedUser.getId()).eq(CommentUser::getCommentId, selectedComment.getId());
            if (!commentUserMapper.exists(wrapper)) {
                CommentUser commentUser = CommentUser.builder().
                        userId(selectedUser.getId())
                        .commentId(selectedComment.getId())
                        .isApproved(new Random().nextDouble() > 0.3)
                        .build();
                commentUserMapper.insert(commentUser);
                currentThumbsUpOrDownNum++;
            }
        }
    }
}
