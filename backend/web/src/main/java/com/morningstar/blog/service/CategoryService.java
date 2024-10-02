package com.morningstar.blog.service;

import com.morningstar.blog.pojo.bo.CategoryDetail;
import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.vo.req.CreateCategoryRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateCategoryRequestVo;
import com.morningstar.constant.R;

import java.util.List;

public interface CategoryService {
    R<Integer> createCategory(CreateCategoryRequestVo vo);

    R<Category> getCategoryById(Integer id);

    R<Object> deleteCategoryById(Integer id);

    R<Object> updateCategoryById(UpdateCategoryRequestVo vo);

    R<List<CategoryDetail>> getAllCategoryDetail();
}
