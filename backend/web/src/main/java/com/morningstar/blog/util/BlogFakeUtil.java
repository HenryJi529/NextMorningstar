package com.morningstar.blog.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.javafaker.Faker;
import com.morningstar.blog.dao.mapper.ArticleMapper;
import com.morningstar.blog.dao.mapper.CategoryMapper;
import com.morningstar.blog.dao.mapper.CommentMapper;
import com.morningstar.blog.dao.mapper.TagMapper;
import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.po.Article;
import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.po.Comment;
import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.infra.constant.SimulationConstant;
import com.morningstar.infra.util.RandomUtil;
import com.morningstar.infra.util.TimeUtil;
import com.morningstar.system.dao.mapper.UserMapper;
import com.morningstar.system.pojo.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class BlogFakeUtil {
    private static final Random random = new Random();
    private static final Faker zhFaker = new Faker(new Locale("zh", "CN"));
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleMapper articleMapper;
    private final CommentMapper commentMapper;

    public Category fakeCategory() {
        String categoryName = RandomUtil.getChineseString(random.nextInt(2) + 4);
        return Category.builder().name(categoryName).build();
    }

    public Tag fakeTag() {
        String tagName = random.nextFloat() > 0.7 ? RandomUtil.getChineseString(random.nextInt(2) + 3) : RandomUtil.getEnglishString(random.nextInt(4) + 3);
        return Tag.builder().name(tagName).build();
    }

    private String fakeTextContent(int minLineNum, int maxLineNum) {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < random.nextInt(maxLineNum - minLineNum) + minLineNum; i++) {
            content.append(zhFaker.lorem().paragraph()).append("\n\n");
        }
        return content.toString();
    }

    public ArticleDetail fakeArticleDetail() {
        Category category = categoryMapper.selectRandomN(1).get(0);
        List<Tag> tags = tagMapper.selectRandomN(random.nextInt(5));

        Date dateCreated = zhFaker.date().past(SimulationConstant.DAY_AFTER_RELEASE, TimeUnit.DAYS);
        long timeDiff = new Date().getTime() - dateCreated.getTime();
        Date dateUpdated = new Date(dateCreated.getTime() + random.nextLong(timeDiff));
        Article article = Article.builder()
                .title(zhFaker.book().title())
                .content(fakeTextContent(10, 20))
                .categoryId(category.getId())
                .views(random.nextInt(100))
                .createTime(TimeUtil.convertDateToLocalDateTime(dateCreated))
                .updateTime(TimeUtil.convertDateToLocalDateTime(dateUpdated))
                .build();
        return ArticleDetail.builder()
                .article(article)
                .category(category)
                .tags(tags)
                .build();
    }

    public Comment fakeComment() {
        User user = userMapper.selectRandomN(1).get(0);
        Article article = articleMapper.selectRandomN(1).get(0);
        Comment parentComment = null;
        if (random.nextFloat() > 0.3) {
            LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Comment::getArticleId, article.getId());
            if (commentMapper.selectCount(wrapper) > 0) {
                parentComment = commentMapper.selectRandomNByArticleId(article.getId(), 1).get(0);
            }
        }
        Date dateCreated;
        if (parentComment != null) {
            long timeDiff = new Date().getTime() - TimeUtil.convertLocalDateTimeToDate(parentComment.getCreateTime()).getTime();
            dateCreated = new Date(TimeUtil.convertLocalDateTimeToDate(parentComment.getCreateTime()).getTime() + random.nextLong(timeDiff));
        } else {
            long timeDiff = new Date().getTime() - TimeUtil.convertLocalDateTimeToDate(article.getCreateTime()).getTime();
            dateCreated = new Date(TimeUtil.convertLocalDateTimeToDate(article.getCreateTime()).getTime() + random.nextLong(timeDiff));
        }
        long timeDiff = new Date().getTime() - dateCreated.getTime();
        Date dateUpdated = new Date(dateCreated.getTime() + random.nextLong(timeDiff));

        return Comment
                .builder()
                .parentId(parentComment != null ? parentComment.getId() : null)
                .userId(user.getId())
                .articleId(article.getId())
                .content(fakeTextContent(1, 3))
                .createTime(TimeUtil.convertDateToLocalDateTime(dateCreated))
                .updateTime(TimeUtil.convertDateToLocalDateTime(dateUpdated))
                .build();
    }

}
