package com.morningstar.blog.pojo.bo;

import com.morningstar.blog.pojo.po.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDoc {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer views;

    private String category;
    private List<String> tags;

    public ArticleDoc(ArticleDetail articleDetail) {
        this.id = articleDetail.getArticle().getId();
        this.title = articleDetail.getArticle().getTitle();
        this.content = articleDetail.getArticle().getContent();
        this.createTime = articleDetail.getArticle().getCreateTime();
        this.updateTime = articleDetail.getArticle().getUpdateTime();
        this.views = articleDetail.getArticle().getViews();

        this.category = articleDetail.getCategory().getName();
        this.tags = articleDetail.getTags().stream().map(Tag::getName).collect(Collectors.toList());
    }
}
