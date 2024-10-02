package com.morningstar.blog.pojo.bo;

import com.morningstar.blog.pojo.po.Article;
import com.morningstar.blog.pojo.po.Category;
import com.morningstar.blog.pojo.po.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetail {
    private Article article;
    private Category category;
    private List<Tag> tags;
}
