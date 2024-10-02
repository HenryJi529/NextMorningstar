package com.morningstar.blog.service;

import com.morningstar.blog.pojo.bo.CategoryDetail;
import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.vo.req.CreateCategoryRequestVo;
import com.morningstar.blog.pojo.vo.req.UpdateCategoryRequestVo;

import java.util.List;

public interface CategoryService {
    Integer createCategory(CreateCategoryRequestVo vo);

    Category getCategoryById(Integer id);

    void deleteCategoryById(Integer id);

    void updateCategoryById(UpdateCategoryRequestVo vo);

    List<CategoryDetail> getAllCategoryDetail();
}
