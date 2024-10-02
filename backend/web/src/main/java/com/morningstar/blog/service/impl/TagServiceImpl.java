package com.morningstar.blog.service.impl;

import cn.hutool.http.HttpStatus;
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
import com.morningstar.constant.R;
import com.morningstar.exception.BaseException;
import com.morningstar.util.EsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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
    public R<Integer> createTag(CreateTagRequestVo vo) {
        Tag tag = Tag.builder().name(vo.getName()).build();
        try{
            tagMapper.insert(tag);
        }catch (DuplicateKeyException e){
            throw new BaseException(String.format("标签名\"%s\"重复", vo.getName()));
        }
        return R.ok(tag.getId());
    }

    @Override
    public R<Tag> getTagById(Integer id) {
        Tag dbTag = tagMapper.selectById(id);
        if (dbTag == null) {
            throw new BaseException(HttpStatus.HTTP_NOT_FOUND, String.format("标签%d不存在", id));
        }
        return R.ok(dbTag);
    }

    private void updateRelatedArticleInEs(Integer tagId, String oldTagName, String newTagName){
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, tagId);
        articleTagMapper.selectList(wrapper).forEach(articleTag -> {
            Long articleId = articleTag.getArticleId();

            List<String> tagNames = esUtil.getDocument("blog_article", articleId.toString(), ArticleDoc.class).getTags();
            for(int i=0; i<tagNames.size(); i++){
                if(tagNames.get(i).equals(oldTagName)){
                    tagNames.set(i, newTagName);
                    break;
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("tags", tagNames);
            esUtil.updateDocument("blog_article", articleId.toString(), map, ArticleDoc.class);
        });

    }

    @Override
    public R<Object> deleteTagById(Integer id) {
        String oldTagName = tagMapper.selectById(id).getName();

        int count = tagMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("标签\"%d\"删除失败", id));
        }
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getTagId, id);
        articleTagMapper.delete(wrapper);

        updateRelatedArticleInEs(id, oldTagName, "");

        return R.ok();
    }

    @Override
    public R<Object> updateTagById(UpdateTagRequestVo vo) {
        String oldTagName = tagMapper.selectById(vo.getId()).getName();

        Tag dbTag = new Tag();
        BeanUtils.copyProperties(vo, dbTag);
        int count = tagMapper.updateById(dbTag);
        if (count != 1) {
            throw new BaseException("标签更新失败");
        }

        updateRelatedArticleInEs(vo.getId(), oldTagName, vo.getName());

        return R.ok();
    }

    @Override
    public R<List<TagDetail>> getAllTagDetail() {
        List<TagDetail> tagDetailList = tagMapper.selectAllTagDetail();
        return R.ok(tagDetailList);
    }
}
