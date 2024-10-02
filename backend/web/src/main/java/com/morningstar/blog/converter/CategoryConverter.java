package com.morningstar.blog.converter;

import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.vo.req.UpdateCategoryRequestVo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryConverter {
    Category updateCategoryRequestVoToCategory(UpdateCategoryRequestVo vo);
}
