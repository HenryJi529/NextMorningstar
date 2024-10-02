package com.morningstar.blog.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("blog_article_tag")
@Schema(description = "博客文章标签映射对象")
public class ArticleTag {
    private Long articleId;
    private Integer tagId;
}
