package com.morningstar.blog.service;

import com.morningstar.blog.pojo.bo.TagDetail;
import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.blog.pojo.vo.req.CreateTagRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateTagRequestVo;

import java.util.List;

public interface TagService {
    Integer createTag(CreateTagRequestVo vo);

    Tag getTagById(Integer id);

    void deleteTagById(Integer id);

    void updateTagById(UpdateTagRequestVo vo);

    List<TagDetail> getAllTagDetail();
}
