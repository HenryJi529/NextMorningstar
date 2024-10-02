package com.morningstar.blog.service;

import com.morningstar.blog.pojo.bo.TagDetail;
import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.blog.pojo.vo.req.CreateTagRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateTagRequestVo;
import com.morningstar.constant.R;

import java.util.List;

public interface TagService {
    R<Integer> createTag(CreateTagRequestVo vo);

    R<Tag> getTagById(Integer id);

    R<Object> deleteTagById(Integer id);

    R<Object> updateTagById(UpdateTagRequestVo vo);

    R<List<TagDetail>> getAllTagDetail();
}
