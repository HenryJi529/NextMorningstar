package com.morningstar.initializer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.blog.dao.mapper.*;
import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.bo.ArticleDoc;
import com.morningstar.blog.pojo.po.*;
import com.morningstar.common.pojo.bo.UserRolePair;
import com.morningstar.kill.dao.mapper.RecordMapper;
import com.morningstar.common.dao.mapper.UserMapper;
import com.morningstar.common.dao.mapper.UserRoleMapper;
import com.morningstar.kill.pojo.po.Record;
import com.morningstar.properties.InitAccountProperties;
import com.morningstar.util.CopyUtil;
import com.morningstar.util.EsUtil;
import com.morningstar.util.FakerUtil;
import com.morningstar.common.pojo.po.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DevDataInitializer extends CommonDataInitializer{

    private final InitAccountProperties initAccountProperties;

    private final FakerUtil fakerUtil;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final UserRoleMapper userRoleMapper;

    private final RecordMapper recordMapper;

    private final CategoryMapper categoryMapper;

    private final TagMapper tagMapper;

    private final ArticleMapper articleMapper;

    private final ArticleTagMapper articleTagMapper;

    private final CommentMapper commentMapper;

    private final CommentUserMapper commentUserMapper;

    private final EsUtil esUtil;

    @Override
    protected void initializeUser() {
        // 添加默认用户
        for(User user: initAccountProperties.getUser()){
            if(userMapper.selectByUsername(user.getUsername()) == null){
                User dbUser = fakerUtil.fakeUser();
                CopyUtil.copyNonNullProperties(user, dbUser);
                dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
                userMapper.insert(dbUser);
                System.out.printf("user: 创建%s...%n", user.getUsername());
            }
        }
        for(UserRolePair userRolePair: initAccountProperties.getUserRole()){
            if(userRoleMapper.exists(userRolePair) == 0){
                userRoleMapper.insert(userRolePair);
                System.out.printf("user-role: 创建 %s-%s...%n", userRolePair.getUsername(), userRolePair.getRoleTag());
            }
        }
        // 添加更多用户
        long currentUserNum = userMapper.selectCount(null);
        while(currentUserNum < 100){
            try{
                User fakerUser = fakerUtil.fakeUser();
                userMapper.insert(fakerUser);
                currentUserNum++;
            }catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    protected void initializeKill(){
        long currentRecordNum = recordMapper.selectCount(null);
        while(currentRecordNum < 10000){
            Record fakeRecord = fakerUtil.fakeKillRecord();
            currentRecordNum += (recordMapper.insertIntoRecordAndUserRecord(fakeRecord)>0?1:0);
        }
    }

    @Override
    protected void initializeBlog() {
        super.initializeBlog();

        String indexName = "blog_article";

        // 生成足够多的分类
        long currentCategoryNum = categoryMapper.selectCount(null);
        while (currentCategoryNum < 6){
            Category category = fakerUtil.fakeBlogCategory();
            try{
                categoryMapper.insert(category);
                currentCategoryNum += 1;
            }catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
        // 生成足够多的标签
        long currentTagNum = tagMapper.selectCount(null);
        while (currentTagNum < 20){
            Tag tag = fakerUtil.fakeBlogTag();
            try{
                tagMapper.insert(tag);
                currentTagNum += 1;
            }catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
        // 生成足够多的文章
        long currentArticleNum = articleMapper.selectCount(null);
        while (currentArticleNum < 100){
            ArticleDetail articleDetail = fakerUtil.fakeBlogArticleDetail();
            Article article = articleDetail.getArticle();
            List<Tag> tags = articleDetail.getTags();

            try{
                // 插入数据库
                articleMapper.insert(article);
                articleTagMapper.insert(tags.stream()
                        .map(tag -> ArticleTag.builder().articleId(article.getId()).tagId(tag.getId()).build())
                        .toList());
                // 插入ElasticSearch
                esUtil.indexDocument(indexName, articleDetail.getArticle().getId().toString(), new ArticleDoc(articleDetail));

                currentArticleNum++;
            }catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

        }
        // 生成足够多的评论
        long currentCommentNum = commentMapper.selectCount(null);
        while (currentCommentNum < 1000){
            Comment comment = fakerUtil.fakeBlogComment();
            try {
                commentMapper.insert(comment);
                currentCommentNum += 1;
            }catch (RuntimeException e){
                System.out.println(e.getMessage());
            }
        }
        // 给评论足够多的点赞与点踩
        long currentThumbsUpOrDownNum = commentUserMapper.selectCount(null);
        while (currentThumbsUpOrDownNum < 2000){
            User selectedUser = userMapper.selectRandomN(1).get(0);
            Comment selectedComment = commentMapper.selectRandomN(1).get(0);
            LambdaQueryWrapper<CommentUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CommentUser::getUserId, selectedUser.getId()).eq(CommentUser::getCommentId, selectedComment.getId());
            if(!commentUserMapper.exists(wrapper)){
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