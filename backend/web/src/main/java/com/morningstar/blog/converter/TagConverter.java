package com.morningstar.blog.converter;

import com.morningstar.blog.pojo.po.Tag;
import com.morningstar.blog.pojo.vo.req.UpdateTagRequestVo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagConverter {
    Tag updateTagRequestVoToTag(UpdateTagRequestVo vo);
}
