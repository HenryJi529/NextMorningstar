package com.morningstar.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.morningstar.blog.converter.CategoryConverter;
import com.morningstar.blog.dao.mapper.ArticleMapper;
import com.morningstar.blog.dao.mapper.CategoryMapper;
import com.morningstar.blog.pojo.bo.ArticleDoc;
import com.morningstar.blog.pojo.bo.CategoryDetail;
import com.morningstar.blog.pojo.po.Article;
import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.vo.req.CreateCategoryRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateCategoryRequestVo;
import com.morningstar.blog.service.CategoryService;
import com.morningstar.constant.ElasticSearchConstant;
import com.morningstar.constant.RedisConstant;
import com.morningstar.response.ResponseCode;
import com.morningstar.exception.BaseException;
import com.morningstar.util.EsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;
    private final EsUtil esUtil;
    private final CategoryConverter categoryConverter;

    @Override
    public Integer createCategory(CreateCategoryRequestVo vo) {
        Category category = Category.builder().name(vo.getName()).build();
        try {
            categoryMapper.insert(category);
        } catch (DuplicateKeyException e) {
            throw new BaseException(ResponseCode.BLOG_CATEGORY_NAME_ALREADY_EXISTS, vo.getName());
        }
        return category.getId();
    }

    @Override
    public Category getCategoryById(Integer id) {
        Category dbCategory = categoryMapper.selectById(id);
        if (dbCategory == null) {
            throw new BaseException(ResponseCode.BLOG_CATEGORY_NOT_FOUND, id);
        }
        return dbCategory;
    }

    private void updateRelatedArticle(Integer categoryId, String categoryName) {
        Map<String, Object> map = new HashMap<>();
        map.put("category", categoryName);

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getCategoryId, categoryId);
        articleMapper.selectList(wrapper).forEach(article -> esUtil.updateDocument(ElasticSearchConstant.BLOG_ARTICLE_INDEX, article.getId().toString(), map, ArticleDoc.class));
    }

    @Override
    @CacheEvict(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, allEntries = true)
    public void deleteCategoryById(Integer id) {
        int count = categoryMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(ResponseCode.BLOG_CATEGORY_DELETE_FAILED, id);
        }

        updateRelatedArticle(id, "");
    }

    @Override
    @CacheEvict(cacheNames = RedisConstant.CACHE_BLOG_ARTICLE, allEntries = true)
    public void updateCategoryById(UpdateCategoryRequestVo vo) {
        Category dbCategory = categoryConverter.updateCategoryRequestVoToCategory(vo);

        int count = categoryMapper.updateById(dbCategory);
        if (count != 1) {
            throw new BaseException("分类更新失败");
        }

        updateRelatedArticle(dbCategory.getId(), vo.getName());
    }

    @Override
    public List<CategoryDetail> getAllCategoryDetail() {
        return categoryMapper.selectAllCategoryDetail();
    }
}
