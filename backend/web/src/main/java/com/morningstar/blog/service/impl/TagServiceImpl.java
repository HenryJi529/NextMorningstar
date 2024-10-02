package com.morningstar.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.blog.dao.mapper.ArticleTagMapper;
import com.morningstar.blog.dao.mapper.TagMapper;
import com.morningstar.blog.pojo.bo.ArticleDoc;
import com.morningstar.blog.pojo.bo.TagDetail;
import com.morningstar.blog.pojo.po.ArticleTag;
import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.blog.pojo.vo.req.CreateTagRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateTagRequestVo;
import com.morningstar.blog.service.TagService;
import com.morningstar.constant.ElasticSearchConstant;
import com.morningstar.constant.RedisConstant;
import com.morningstar.exception.BaseException;
import com.morningstar.exception.BlogTagNotFoundException;
import com.morningstar.util.EsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final EsUtil esUtil;

    @Override
    public Integer createTag(CreateTagRequestVo vo) {
        Tag tag = Tag.builder().name(vo.getName()).build();
        try {
            tagMapper.insert(tag);
        } catch (DuplicateKeyException e) {
            throw new BaseException(String.format("标签名\"%s\"重复", vo.getName()));
        }
        return tag.getId();
    }

    @Override
    public Tag getTagById(Integer id) {
        Tag dbTag = tagMapper.selectById(id);
        if (dbTag == null) {
            throw new BlogTagNotFoundException(id);
        }
        return dbTag;
    }

    private void updateRelatedArticleInEs(Integer tagId, String oldTagName, String newTagName) {
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, tagId);
        articleTagMapper.selectList(wrapper).forEach(articleTag -> {
            Long articleId = articleTag.getArticleId();

            ArticleDoc articleDoc = esUtil.getDocument(ElasticSearchConstant.BLOG_ARTICLE_INDEX, articleId.toString(), ArticleDoc.class);
            if (articleDoc != null) {
                List<String> tagNames = articleDoc.getTags();
                for (int i = 0; i < tagNames.size(); i++) {
                    if (tagNames.get(i).equals(oldTagName)) {
                        tagNames.set(i, newTagName);
                        break;
                    }
                }
                Map<String, Object> map = new HashMap<>();
                map.put("tags", tagNames);
                esUtil.updateDocument(ElasticSearchConstant.BLOG_ARTICLE_INDEX, articleId.toString(), map, ArticleDoc.class);
            }
        });

    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, allEntries = true)
    public void deleteTagById(Integer id) {
        String oldTagName = tagMapper.selectById(id).getName();

        int count = tagMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("标签\"%d\"删除失败", id));
        }
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, id);
        articleTagMapper.delete(wrapper);

        updateRelatedArticleInEs(id, oldTagName, "");
    }

    @Override
    @CacheEvict(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, allEntries = true)
    public void updateTagById(UpdateTagRequestVo vo) {
        String oldTagName = tagMapper.selectById(vo.getId()).getName();

        Tag dbTag = new Tag();
        BeanUtils.copyProperties(vo, dbTag);
        int count = tagMapper.updateById(dbTag);
        if (count != 1) {
            throw new BaseException("标签更新失败");
        }

        updateRelatedArticleInEs(vo.getId(), oldTagName, vo.getName());
    }

    @Override
    public List<TagDetail> getAllTagDetail() {
        return tagMapper.selectAllTagDetail();
    }
}
