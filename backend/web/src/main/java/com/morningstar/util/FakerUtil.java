package com.morningstar.util;

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
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.pojo.bo.Place;
import com.morningstar.common.pojo.po.User;
import com.morningstar.kill.room.Killer;
import com.morningstar.kill.game.GameLog;
import com.morningstar.kill.game.Mode;
import com.morningstar.kill.pojo.po.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FakerUtil {
    private static final Faker zhFaker = new Faker(new Locale("zh", "CN"));
    private static final Faker enFaker = new Faker(new Locale("en", "US"));
    private static final int daysAfterRelease = 100;
    private final Random random = new Random();

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final CategoryMapper categoryMapper;

    private final TagMapper tagMapper;

    private final ArticleMapper articleMapper;

    private final CommentMapper commentMapper;

    public String getRandomNickname() {
        return Place.getRandomPlace().name() + zhFaker.name().fullName();
    }

    public String getRandomNickname(double latitude, double longitude) {
        return Place.getNearestPlace(latitude, longitude).name() + zhFaker.name().fullName();
    }

    public User fakeUser() {
        return fakeUser(daysAfterRelease, TimeUnit.DAYS);
    }

    public User fakeUser(int atMost, TimeUnit timeUnit) {
        Date dateCreated = zhFaker.date().past(atMost, timeUnit);
        long timeDiff = new Date().getTime() - dateCreated.getTime();
        Date dateUpdated = new Date(dateCreated.getTime() + random.nextLong(timeDiff));

        return User
                .builder()
                .id(UUID.randomUUID())
                .username(enFaker.name().username().replace(".", ""))
                .password(passwordEncoder.encode(enFaker.internet().password()))
                .email(enFaker.internet().emailAddress())
                .nickname(getRandomNickname())
                .avatar(random.nextFloat() > 0.2 ? UUID.randomUUID() + ".jpg" : null)
                .createTime(TimeUtil.convertDateToLocalDateTime(dateCreated))
                .updateTime(TimeUtil.convertDateToLocalDateTime(dateUpdated))
                .build();
    }

    public Category fakeBlogCategory() {
        String categoryName = RandomUtil.getChineseString(random.nextInt(2) + 4);
        return Category.builder().name(categoryName).build();
    }

    public Tag fakeBlogTag() {
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

    public ArticleDetail fakeBlogArticleDetail() {
        Category category = categoryMapper.selectRandomN(1).get(0);
        List<Tag> tags = tagMapper.selectRandomN(random.nextInt(5));

        Date dateCreated = zhFaker.date().past(daysAfterRelease, TimeUnit.DAYS);
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

    public Comment fakeBlogComment() {
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

    public Record fakeKillRecord() {
        // 默认最迟创建时间只考虑游戏中玩家的最晚创建时间
        return fakeKillRecord(0);
    }

    public Record fakeKillRecord(long earliestCreateMilliseconds) {
        // 确定玩家人数
        Mode mode = Mode.getRandomMode();
        int minHeadNum = mode.getMinHeadNum();
        int maxHeadNum = mode.getMaxHeadNum();
        int headNum = minHeadNum != maxHeadNum ? random.nextInt(maxHeadNum - minHeadNum) + minHeadNum : minHeadNum;
        List<User> users = userMapper.selectRandomN(headNum);
        List<User> winners = users.stream().limit((headNum > 2 ? random.nextInt(headNum - 2) : 0) + 1).toList(); // NOTE: 赢家数难确定(当前设置为1~N-1)
        GameLog gameLog = new GameLog(mode, users.stream().map(Killer::new).collect(Collectors.toList()), winners.stream().map(Killer::new).collect(Collectors.toList()));

        // 游戏开始时间应要迟于最晚创建的用户的创建时间
        LocalDateTime lastUserCreateTime = users.stream().map(User::getCreateTime).max((localDateTime1, localDateTime2) -> {
            Date date1 = TimeUtil.convertLocalDateTimeToDate(localDateTime1);
            Date date2 = TimeUtil.convertLocalDateTimeToDate(localDateTime2);
            return date1.compareTo(date2);
        }).orElse(TimeUtil.getCurrentLocalDateTime().minusSeconds(1));

        earliestCreateMilliseconds = Math.max(TimeUtil.convertLocalDateTimeToDate(lastUserCreateTime).getTime(), earliestCreateMilliseconds);
        long timeDiff = System.currentTimeMillis() - earliestCreateMilliseconds;
        Date dateCreated = new Date(random.nextLong(timeDiff) + earliestCreateMilliseconds);

        // 游戏时长一般不会超过一小时, 且结束时间不会超过当前时间
        Date now = new Date();
        Date dateUpdated = new Date(dateCreated.getTime() + random.nextLong(60 * 60 * 1000));
        dateUpdated = dateUpdated.getTime() > now.getTime() ? now : dateUpdated;

        return Record.builder()
                .id(UUID.randomUUID())
                .content(gameLog)
                .createTime(TimeUtil.convertDateToLocalDateTime(dateCreated))
                .updateTime(TimeUtil.convertDateToLocalDateTime(dateUpdated))
                .build();
    }
}
