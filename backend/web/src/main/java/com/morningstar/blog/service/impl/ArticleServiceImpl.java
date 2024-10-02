package com.morningstar.blog.service.impl;

import cn.hutool.core.util.CharUtil;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import com.morningstar.blog.properties.BlogProperties;
import com.morningstar.blog.service.ArticleService;
import com.morningstar.infra.constant.ElasticSearchConstant;
import com.morningstar.infra.constant.RedisConstant;
import com.morningstar.infra.exception.BaseException;
import com.morningstar.infra.response.PageResult;
import com.morningstar.infra.response.ResponseCode;
import com.morningstar.infra.util.EsUtil;
import com.morningstar.infra.util.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final BlogProperties blogProperties;
    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final EsUtil esUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatClient chatClient;

    private void setExcerptAsContent(ArticleDetail articleDetail) {
        Article article = articleDetail.getArticle();
        article.setContent(
                extractExcerpt(
                        article.getContent(),
                        blogProperties.getExcerptMaxSentenceNum(),
                        blogProperties.getExcerptMaxVisualLength()
                )
        );
    }

    private String extractExcerpt(String markdown, int maxSentenceNum, double maxVisualLength) {
        String html = TextUtil.convertMarkdown2Html(markdown);

        // 将 HTML 转换为文本
        String text = html.replaceAll("<[^>]+>", "").trim();
        StringBuilder sb = new StringBuilder();

        // 先根据句子长度构建摘要
        double excerptWordNum = 0;
        String[] sentences = text.split("\n");
        for (int i = 0; i < sentences.length && i < maxSentenceNum; i++) {
            sb.append(sentences[i]).append("&nbsp;&nbsp;");

            double currentVisualLength = 0.0;
            for (char c : sentences[i].toCharArray()) {
                currentVisualLength += CharUtil.isAscii(c) ? 0.5 : 1;
            }
            excerptWordNum += currentVisualLength;
            // 判断视觉长度是否足够
            if (excerptWordNum >= maxVisualLength) {
                break;
            }
        }

        return sb + "...";
    }

    @Override
    @Cacheable(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, key = "#id")
    public ArticleDetail getArticleDetailByArticleId(Long id) {
        ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(id);
        if (articleDetail == null) {
            throw new BaseException(ResponseCode.BLOG_ARTICLE_NOT_FOUND, id);
        }
        return articleDetail;
    }

    @Override
    public void recordArticleView(Long id) {
        redisTemplate.opsForValue().increment(RedisConstant.BLOG_VIEWS + RedisConstant.KEY_SEPARATOR + id);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    @CacheEvict(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, key = "#id")
    public void syncArticleViewByArticleId(Long id, int newViews) {
        // 直接SQL原子更新，绝对并发安全
        int count = articleMapper.update(new LambdaUpdateWrapper<Article>()
                .setSql("views = views + " + newViews)
                .eq(Article::getId, id));
        if (count == 0) {
            throw new BaseException(ResponseCode.BLOG_ARTICLE_NOT_FOUND, id);
        }
        // 同步到 ES
        ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(id);
        esUtil.indexDocument(
                ElasticSearchConstant.BLOG_ARTICLE_INDEX,
                id.toString(),
                new ArticleDoc(articleDetail)
        );
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
            throw new BaseException(ResponseCode.BLOG_ARTICLE_NOT_FOUND, articleId);
        }
    }

    private void checkCategoryExists(Integer categoryId) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getId, categoryId);
        if (!categoryMapper.exists(categoryLambdaQueryWrapper)) {
            throw new BaseException(ResponseCode.BLOG_CATEGORY_NOT_FOUND, categoryId);
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

        // 删除旧摘要
        redisTemplate.delete(RedisConstant.BLOG_SUMMARY + RedisConstant.KEY_SEPARATOR + article.getId());

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

        redisTemplate.delete(RedisConstant.BLOG_VIEWS + RedisConstant.KEY_SEPARATOR + id);
        redisTemplate.delete(RedisConstant.BLOG_SUMMARY + RedisConstant.KEY_SEPARATOR + id);

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
    public void refreshArticleSearch() {
        String indexName = ElasticSearchConstant.BLOG_ARTICLE_INDEX;
        List<Article> articles = articleMapper.selectList(null);
        for (Article article : articles) {
            ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(article.getId());
            esUtil.indexDocument(indexName, article.getId().toString(), new ArticleDoc(articleDetail));
        }
    }

    private String generateArticleSummaryByArticleId(Long id) {
        ArticleDetail articleDetail = articleMapper.selectArticleDetailByArticleId(id);
        if (articleDetail == null) {
            throw new BaseException(ResponseCode.BLOG_ARTICLE_NOT_FOUND, id);
        }
        String prompt = """
                你是专业文章摘要助手。请根据下面博客内容生成一段中文摘要，
                要求：
                1. 只输出摘要正文，不要解释、不要标题、不要Markdown
                2. 语言通顺，提炼核心观点
                3. 控制在 80-120 字
                文章：
                # %s
                %s
                分类：%s
                标签：%s
                """.formatted(
                articleDetail.getArticle().getTitle(),
                articleDetail.getArticle().getContent(),
                articleDetail.getCategory().getName(),
                articleDetail.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "))
        );
        String response = chatClient.prompt(prompt).call().content();
        return response != null ? response : "";
    }

    @Override
    public String getArticleSummaryByArticleId(Long id) {
        String key = RedisConstant.BLOG_SUMMARY + RedisConstant.KEY_SEPARATOR + id;
        String summary = (String) redisTemplate.opsForValue().get(key);
        if (summary == null) {
            summary = generateArticleSummaryByArticleId(id);
            redisTemplate.opsForValue().set(key, summary);
        }
        return summary;
    }

    @Override
    @Async
    public void refreshArticleSummary() {
        Set<String> keys = redisTemplate.keys(RedisConstant.BLOG_SUMMARY + RedisConstant.KEY_SEPARATOR + "*");
        for (String key : keys) {
            String[] parts = key.split(RedisConstant.KEY_SEPARATOR);
            Long id = Long.parseLong(parts[parts.length - 1]);
            redisTemplate.opsForValue().set(key, generateArticleSummaryByArticleId(id));
        }
    }
}
