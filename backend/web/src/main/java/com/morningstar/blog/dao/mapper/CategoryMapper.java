package com.morningstar.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.blog.pojo.bo.CategoryDetail;
import com.morningstar.blog.pojo.po.Category;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {
    List<Category> selectRandomN(int N);

    List<CategoryDetail> selectAllCategoryDetail();
}
