package com.morningstar.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.blog.pojo.bo.TagDetail;
import com.morningstar.blog.pojo.po.Tag;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    List<Tag> selectRandomN(int N);

    List<TagDetail> selectAllTagDetail();
}
