package com.morningstar.blog.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.blog.dao.mapper.ArticleMapper;
import com.morningstar.blog.dao.mapper.ArticleTagMapper;
import com.morningstar.blog.dao.mapper.CategoryMapper;
import com.morningstar.blog.dao.mapper.TagMapper;
import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.blog.pojo.bo.ArticleDoc;
import com.morningstar.blog.pojo.po.Article;
import com.morningstar.blog.pojo.po.ArticleTag;
import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.blog.pojo.vo.req.CreateArticleRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateArticleRequestVo;
import com.morningstar.blog.service.ArticleService;
import com.morningstar.constant.ElasticSearchConstant;
import com.morningstar.constant.PageResult;
import com.morningstar.constant.RedisConstant;
import com.morningstar.exception.BaseException;
import com.morningstar.exception.BlogArticleNotFoundException;
import com.morningstar.exception.BlogCategoryNotFoundException;
import com.morningstar.properties.BlogProperties;
import com.morningstar.util.EsUtil;
import com.morningstar.util.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArticleServiceImpl implements ArticleService {
    private final BlogProperties blogProperties;
    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final EsUtil esUtil;

    private void setExcerptAsContent(ArticleDetail articleDetail) {
        Article article = articleDetail.getArticle();
        article.setContent(
                extractExcerpt(
                        article.getContent(),
                        blogProperties.getExcerptSentences(),
                        blogProperties.getExcerptWords()
                )
        );
    }

    private String extractExcerpt(String markdown, int sentences, int words) {
        String html = TextUtil.convertMarkdown2Html(markdown);

        // 将 HTML 转换为文本
        String text = html.replaceAll("<[^>]+>", "").trim();
        StringBuilder sb = new StringBuilder();

        // 先根据句子长度构建摘要
        String[] sentencesArray = text.split("\n");
        List<String> sentencesList = List.of(sentencesArray);
        for (int i = 0; i < sentences && i < sentencesList.size(); i++) {
            sb.append(sentencesList.get(i)).append("  ");
        }
        // 判断字数是否足够
        if (sb.toString().trim().length() > words) {
            return sb.toString().trim();
        } else {
            return text.replace("\n", " ").substring(0, Math.min(words, text.length())).trim();
        }
    }

    @Override
    @Cacheable(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, key = "#id")
    public ArticleDetail getArticleDetailByArticleId(Long id) {
        ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(id);
        if (articleDetail == null) {
            throw new BlogArticleNotFoundException(id);
        } else {
            Article article = articleDetail.getArticle();
            article.setViews(article.getViews() + 1);
            articleMapper.updateById(article);
            return articleDetail;
        }
    }

    @Override
    public PageResult<ArticleDetail> getAllArticleDetail(int pageNum, int pageSize) {
        List<ArticleDetail> articleDetailList = articleMapper.selectAllArticleDetail(pageSize, pageSize * (pageNum - 1));
        articleDetailList.forEach(this::setExcerptAsContent);
        long totalRecordNum = articleMapper.selectCount(null);

        return new PageResult<>(articleDetailList, pageNum, pageSize, totalRecordNum);
    }

    @Override
    public PageResult<ArticleDetail> getArticleDetailByCategoryId(int id, int pageNum, int pageSize) {
        List<ArticleDetail> articleDetailList = articleMapper.selectArticleDetailByCategoryId(id, pageSize, pageSize * (pageNum - 1));
        articleDetailList.forEach(this::setExcerptAsContent);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCategoryId, id);
        long totalRecordNum = articleMapper.selectCount(wrapper);

        return new PageResult<>(articleDetailList, pageNum, pageSize, totalRecordNum);
    }

    @Override
    public PageResult<ArticleDetail> getArticleDetailByTagId(int id, int pageNum, int pageSize) {
        List<ArticleDetail> articleDetailList = articleMapper.selectArticleDetailByTagId(id, pageSize, pageSize * (pageNum - 1));
        articleDetailList.forEach(this::setExcerptAsContent);

        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, id);
        long totalRecordNum = articleTagMapper.selectCount(wrapper);

        return new PageResult<>(articleDetailList, pageNum, pageSize, totalRecordNum);
    }

    private List<ArticleTag> getArticleTagList(Long articleId, List<Integer> tagIds) {
        return tagIds.stream().map(tagId -> ArticleTag
                .builder()
                .articleId(articleId)
                .tagId(tagId)
                .build()).toList();
    }

    private void checkArticleExists(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogArticleNotFoundException(articleId);
        }
    }

    private void checkCategoryExists(Integer categoryId) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getId, categoryId);
        if (!categoryMapper.exists(categoryLambdaQueryWrapper)) {
            throw new BlogCategoryNotFoundException(categoryId);
        }
    }

    private void checkTagExists(List<Integer> tagIds) {
        List<Integer> missed = new ArrayList<>();
        for (Integer tagId : tagIds) {
            LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            tagLambdaQueryWrapper.eq(Tag::getId, tagId);
            if (!tagMapper.exists(tagLambdaQueryWrapper)) {
                missed.add(tagId);
            }
        }
        if (!missed.isEmpty()) {
            throw new BaseException(String.join("; ", missed.stream().map(tagId -> String.format("标签%d不存在", tagId)).toList()));
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String createArticle(CreateArticleRequestVo vo) {
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
        } catch (DuplicateKeyException e) {
            throw new BaseException(String.format("博客标题\"%s\"重复", vo.getTitle()));
        }
        if (vo.getTagIds() != null && !vo.getTagIds().isEmpty()) {
            List<ArticleTag> articleTagList = getArticleTagList(article.getId(), vo.getTagIds());
            articleTagMapper.insert(articleTagList);
        }

        ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(article.getId());
        esUtil.indexDocument(ElasticSearchConstant.BLOG_ARTICLE_INDEX, article.getId().toString(), new ArticleDoc(articleDetail));

        return article.getId().toString();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    @CacheEvict(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, key = "#vo.id")
    public void updateArticle(UpdateArticleRequestVo vo) {
        checkArticleExists(vo.getId());
        checkCategoryExists(vo.getCategoryId());
        checkTagExists(vo.getTagIds());

        Article article = Article
                .builder()
                .id(vo.getId())
                .title(vo.getTitle())
                .content(vo.getContent())
                .categoryId(vo.getCategoryId())
                .build();

        try {
            articleMapper.updateById(article);
        } catch (DuplicateKeyException e) {
            throw new BaseException(String.format("博客标题\"%s\"重复", vo.getTitle()));
        }

        // 重新创建对应标签
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, article.getId());
        articleTagMapper.delete(wrapper);
        List<ArticleTag> articleTagList = getArticleTagList(article.getId(), vo.getTagIds());
        for (ArticleTag articleTag : articleTagList) {
            articleTagMapper.insert(articleTag);
        }

        ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(article.getId());
        esUtil.indexDocument(ElasticSearchConstant.BLOG_ARTICLE_INDEX, article.getId().toString(), new ArticleDoc(articleDetail));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    @CacheEvict(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, key = "#id")
    public void deleteArticle(Long id) {
        int count = articleMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("文章\"%d\"删除失败", id));
        }
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, id);
        articleTagMapper.delete(wrapper);

        esUtil.deleteDocument(ElasticSearchConstant.BLOG_ARTICLE_INDEX, id.toString());
    }

    @Override
    public String getRandomArticleId() {
        return articleMapper.selectRandomN(1).get(0).getId().toString();
    }

    @Override
    public PageResult<ArticleDetail> getArticleDetailByTerm(String term, int pageNum, int pageSize) {
        Query byAll = MatchQuery.of(m -> m
                .field("all")
                .query(term)
        )._toQuery();
        Query byTags = TermQuery.of(t -> t
                .field("tags")
                .value(term)
        )._toQuery();
        Query byCategory = TermQuery.of(t -> t
                .field("category")
                .value(term)
        )._toQuery();
        String htmlTag = "blog-search-term";
        HighlightField highlightField = HighlightField.of(f -> f.preTags("<" + htmlTag + ">").postTags("</" + htmlTag + ">").requireFieldMatch(false));


        EsUtil.SearchResult<ArticleDoc> searchResult = esUtil.searchDocument(
                s -> s
                        .index(ElasticSearchConstant.BLOG_ARTICLE_INDEX)
                        .query(q -> q
                                .bool(
                                        b -> b
                                                .should(byAll)
                                                .should(byTags)
                                                .should(byCategory)
                                )
                        )
                        .from((pageNum - 1) * pageSize).size(pageSize)
                        .highlight(h -> h.fields("tags", highlightField).fields("category", highlightField).fields("title", highlightField).fields("content", highlightField)
                        ), ArticleDoc.class);

        List<ArticleDoc> articleDocList = searchResult.getRecords();
        List<Map<String, List<String>>> highlights = searchResult.getHighlights();

        List<ArticleDetail> articleDetailList = new ArrayList<>();
        if (articleDocList != null && !articleDocList.isEmpty()) {
            List<Long> articleIds = articleDocList.stream().map(ArticleDoc::getId).toList();
            for (int i = 0; i < articleIds.size(); i++) {
                // 获得内容为摘要的articleDetail
                Long articleId = articleIds.get(i);
                ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(articleId);
                setExcerptAsContent(articleDetail);

                Map<String, List<String>> highlight = highlights.get(i);

                for (Map.Entry<String, List<String>> entry : highlight.entrySet()) {
                    switch (entry.getKey()) {
                        case "tags" -> // 标签高亮只会有一个
                                articleDetail.getTags().forEach(tag -> {
                                    if (entry.getValue().get(0).contains(tag.getName())) {
                                        tag.setName(entry.getValue().get(0));
                                    }
                                });
                        case "category" -> // 分类高亮只会有一个
                                articleDetail.getCategory().setName(entry.getValue().get(0));
                        case "title" -> // 标题高亮只会有一个
                                articleDetail.getArticle().setTitle(entry.getValue().get(0));
                        case "content" -> {
                            // 内容高亮不一定只有一个
                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < entry.getValue().size(); j++) {
                                sb.append(entry.getValue().get(j));
                                sb.append(" ");
                                if (sb.toString().length() > 200 + (2 * htmlTag.length() + 3) * (i + 2)) {
                                    // 限制纯内容长度
                                    break;
                                }
                            }
                            articleDetail.getArticle().setContent(sb.toString());
                        }
                    }
                }

                articleDetailList.add(articleDetail);
            }
        }

        return new PageResult<>(articleDetailList, pageNum, pageSize, searchResult.getTotalRecordNum());
    }

    @Override
    public void refreshArticleIndex() {
        String indexName = ElasticSearchConstant.BLOG_ARTICLE_INDEX;
        List<Article> articles = articleMapper.selectList(null);
        for (Article article : articles) {
            ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(article.getId());
            esUtil.indexDocument(indexName, article.getId().toString(), new ArticleDoc(articleDetail));
        }
    }
}
