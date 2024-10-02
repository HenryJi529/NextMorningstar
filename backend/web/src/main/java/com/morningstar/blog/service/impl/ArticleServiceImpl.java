package com.morningstar.blog.service.impl;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.blog.dao.mapper.ArticleMapper;
import com.morningstar.blog.dao.mapper.ArticleTagMapper;
import com.morningstar.blog.dao.mapper.CategoryMapper;
import com.morningstar.blog.dao.mapper.TagMapper;
import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.po.Article;
import com.morningstar.blog.pojo.po.ArticleTag;
import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.blog.pojo.vo.req.CreateArticleRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateArticleRequestVo;
import com.morningstar.blog.service.ArticleService;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.R;
import com.morningstar.exception.BaseException;
import com.morningstar.properties.BlogProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleServiceImpl implements ArticleService {
    private final BlogProperties blogProperties;
    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    private void setExcerptAsContent(ArticleDetail articleDetail) {
        Article article = articleDetail.getArticle();
        article.setContent(extractExcerpt(article.getContent(), blogProperties.getExcerptLength()));
    }

    private static String extractExcerpt(String markdown, int sentences) {
        // 解析 Markdown
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);

        // 渲染为 HTML 以便提取文本
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        // 将 HTML 转换为文本
        String text = html.replaceAll("<[^>]+>", "").trim();

        // 分割文本为句子
        String[] sentencesArray = text.split("\n");
        List<String> sentencesList = List.of(sentencesArray);

        // 构建摘要
        StringBuilder excerpt = new StringBuilder();
        for (int i = 0; i < sentences && i < sentencesList.size(); i++) {
            excerpt.append(sentencesList.get(i)).append("  ");
        }
        return excerpt.toString().trim();
    }

    @Override
    public R<ArticleDetail> getArticleDetailByArticleId(Long id) {
        ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(id);
        if(articleDetail == null){
            throw new BaseException(HttpStatus.HTTP_NOT_FOUND, String.format("文章%d不存在", id));
        }else {
            Article article = articleDetail.getArticle();
            article.setViews(article.getViews() + 1);
            articleMapper.updateById(article);
            return R.ok(articleDetail);
        }
    }

    @Override
    public R<PageResult<ArticleDetail>> getAllArticleDetail(int pageNum, int pageSize) {
        List<ArticleDetail> articleDetailList = articleMapper.selectAllArticleDetail(pageSize, pageSize * (pageNum - 1));
        articleDetailList.forEach(this::setExcerptAsContent);
        long totalRecordNum = articleMapper.selectCount(null);

        return R.ok(new PageResult<>(articleDetailList, pageNum, pageSize, totalRecordNum));
    }

    @Override
    public R<PageResult<ArticleDetail>> getArticleDetailByCategoryId(int id, int pageNum, int pageSize) {
        List<ArticleDetail> articleDetailList = articleMapper.selectArticleDetailByCategoryId(id, pageSize, pageSize * (pageNum - 1));
        articleDetailList.forEach(this::setExcerptAsContent);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCategoryId, id);
        long totalRecordNum = articleMapper.selectCount(wrapper);

        return R.ok(new PageResult<>(articleDetailList, pageNum, pageSize, totalRecordNum));
    }

    @Override
    public R<PageResult<ArticleDetail>> getArticleDetailByTagId(int id, int pageNum, int pageSize) {
        List<ArticleDetail> articleDetailList = articleMapper.selectArticleDetailByTagId(id, pageSize, pageSize * (pageNum - 1));
        articleDetailList.forEach(this::setExcerptAsContent);

        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, id);
        long totalRecordNum = articleTagMapper.selectCount(wrapper);

        return R.ok(new PageResult<>(articleDetailList, pageNum, pageSize, totalRecordNum));
    }

    private List<ArticleTag> getArticleTagList(Long articleId, List<Integer> tagIds){
        return tagIds.stream().map(tagId -> ArticleTag
                .builder()
                .articleId(articleId)
                .tagId(tagId)
                .build()).toList();
    }


    private void checkCategoryExists(Integer categoryId) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getId, categoryId);
        if(!categoryMapper.exists(categoryLambdaQueryWrapper)){
            throw new BaseException("分类不存在");
        }
    }

    private void checkTagExists(List<Integer> tagIds) {
        List<Integer> missed = new ArrayList<>();
        for (Integer tagId : tagIds) {
            LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tagLambdaQueryWrapper.eq(Tag::getId, tagId);
            if(!tagMapper.exists(tagLambdaQueryWrapper)){
                missed.add(tagId);
            }
        }
        if(!missed.isEmpty()){
            throw new BaseException(String.join("; ", missed.stream().map(tagId -> String.format("标签%d不存在", tagId)).toList()));
        }
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public R<Long> createArticle(CreateArticleRequestVo vo) {
        checkCategoryExists(vo.getCategoryId());
        checkTagExists(vo.getTagIds());

        Article article = Article
                .builder()
                .title(vo.getTitle())
                .content(vo.getContent())
                .categoryId(vo.getCategoryId())
                .build();

        try {
            articleMapper.insert(article);
        }catch (DuplicateKeyException e){
            throw new BaseException(String.format("博客标题\"%s\"重复", vo.getTitle()));
        }
        if (vo.getTagIds() != null && !vo.getTagIds().isEmpty()) {
            List<ArticleTag> articleTagList = getArticleTagList(article.getId(), vo.getTagIds());
            articleTagMapper.insert(articleTagList);
        }
        return R.ok(article.getId());
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public R<Object> updateArticle(UpdateArticleRequestVo vo) {
        checkCategoryExists(vo.getCategoryId());
        checkTagExists(vo.getTagIds());

        Article article = Article
                .builder()
                .id(vo.getId())
                .title(vo.getTitle())
                .content(vo.getContent())
                .categoryId(vo.getCategoryId())
                .build();

        try{
            articleMapper.updateById(article);
        }catch (DuplicateKeyException e){
            throw new BaseException(String.format("博客标题\"%s\"重复", vo.getTitle()));
        }
        if (vo.getTagIds() != null && !vo.getTagIds().isEmpty()) {
            List<ArticleTag> articleTagList = getArticleTagList(article.getId(), vo.getTagIds());
            for (ArticleTag articleTag : articleTagList) {
                LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(ArticleTag::getArticleId, article.getId());
                wrapper.eq(ArticleTag::getTagId, articleTag.getTagId());
                if(!articleTagMapper.exists(wrapper)){
                    articleTagMapper.insert(articleTag);
                }
            }
        }
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public R<Object> deleteArticle(Long id) {
        int count = articleMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("文章\"%d\"删除失败", id));
        }
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, id);
        articleTagMapper.delete(wrapper);
        return R.ok();
    }

    @Override
    public R<String> getRandomArticleId() {
        return R.ok(articleMapper.selectRandomN(1).get(0).getId().toString());
    }

    @Override
    public R<PageResult<ArticleDetail>> getArticleDetailByTerm(String term, int pageNum, int pageSize) {
        List<ArticleDetail> articleDetailList = articleMapper.selectArticleDetailByTerm(term, pageSize, pageSize * (pageNum - 1));
        articleDetailList.forEach(this::setExcerptAsContent);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Article::getTitle, term).or().like(Article::getContent, term);
        long totalRecordNum = articleMapper.selectCount(wrapper);

        return R.ok(new PageResult<>(articleDetailList, pageNum, pageSize, totalRecordNum));
    }
}
