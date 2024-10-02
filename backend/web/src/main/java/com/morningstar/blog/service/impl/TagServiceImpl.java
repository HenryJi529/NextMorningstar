package com.morningstar.blog.service.impl;

import cn.hutool.http.HttpStatus;
import com.morningstar.blog.dao.mapper.TagMapper;
import com.morningstar.blog.pojo.bo.TagDetail;
import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.blog.pojo.vo.req.CreateTagRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateTagRequestVo;
import com.morningstar.blog.service.TagService;
import com.morningstar.constant.R;
import com.morningstar.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

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

    @Override
    public R<Object> deleteTagById(Integer id) {
        int count = tagMapper.deleteById(id);
        if (count != 1) {
            throw new BaseException(String.format("标签\"%d\"删除失败", id));
        }
        return R.ok();
    }

    @Override
    public R<Object> updateTagById(UpdateTagRequestVo vo) {
        Tag dbTag = new Tag();
        BeanUtils.copyProperties(vo, dbTag);
        int count = tagMapper.updateById(dbTag);
        if (count != 1) {
            throw new BaseException("标签更新失败");
        }
        return R.ok();
    }

    @Override
    public R<List<TagDetail>> getAllTagDetail() {
        List<TagDetail> tagDetailList = tagMapper.selectAllTagDetail();
        return R.ok(tagDetailList);
    }
}
