package com.morningstar.blog.web.controller;

import com.morningstar.blog.dao.mapper.ArticleMapper;
import com.morningstar.blog.pojo.bo.ArticleDetail;
import com.morningstar.properties.BlogProperties;
import com.morningstar.properties.SiteProperties;
import com.morningstar.util.TextUtil;
import com.morningstar.util.TimeUtil;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Link;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Item;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.feed.AbstractAtomFeedView;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "博客订阅相关接口定义")
@RestController
@RequestMapping("/blog/feed")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedController {

    private final ArticleMapper articleMapper;

    private final BlogProperties blogProperties;

    private final SiteProperties siteProperties;

    private String getBlogRootUrl() {
        return "https://" + siteProperties.getDomain() + blogProperties.getRootPath();
    }

    private List<ArticleDetail> getArticleDetailList() {
        return articleMapper.selectAllArticleDetail(100, 0);
    }

    private String getArticleTitle(ArticleDetail articleDetail) {
        if (articleDetail.getCategory() != null) {
            return String.format("[%s] %s", articleDetail.getCategory().getName(), articleDetail.getArticle().getTitle());
        } else {
            return articleDetail.getArticle().getTitle();
        }
    }

    private String getArticleUrl(ArticleDetail articleDetail) {
        return getBlogRootUrl() + "/" + articleDetail.getArticle().getId();
    }

    private String getArticleHtmlContent(ArticleDetail articleDetail) {
        return TextUtil.convertMarkdown2Html(articleDetail.getArticle().getContent());
    }

    @Operation(summary = "RSS订阅")
    @GetMapping("/rss.xml")
    public View getRss() {
        return new AbstractRssFeedView() {
            @Override
            protected void buildFeedMetadata(@NonNull Map<String, Object> model,
                                             @NonNull Channel feed, @NonNull HttpServletRequest request) {
                feed.setTitle(blogProperties.getRssTitle());
                feed.setDescription(blogProperties.getDescription());
                feed.setLink(getBlogRootUrl());
                feed.setPubDate(new Date());
            }

            @NonNull
            @Override
            protected List<Item> buildFeedItems(@NonNull Map<String, Object> model, @NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
                List<ArticleDetail> articleDetailList = getArticleDetailList();
                return articleDetailList.stream().map(articleDetail -> {
                    Item item = new Item();

                    item.setTitle(getArticleTitle(articleDetail));
                    item.setLink(getArticleUrl(articleDetail));
                    item.setPubDate(TimeUtil.convertLocalDateTimeToDate(articleDetail.getArticle().getUpdateTime()));

                    Content content = new Content();
                    content.setType(Content.HTML);
                    content.setValue(getArticleHtmlContent(articleDetail));
                    item.setContent(content);

                    return item;
                }).collect(Collectors.toList());
            }
        };
    }

    @Operation(summary = "Atom订阅")
    @GetMapping("/atom.xml")
    public View getAtom() {
        return new AbstractAtomFeedView() {
            @Override
            protected void buildFeedMetadata(@NonNull Map<String, Object> model, @NonNull Feed feed, @NonNull HttpServletRequest request) {
                feed.setTitle(blogProperties.getAtomTitle());

                Link link = new Link();
                link.setHref(getBlogRootUrl());
                link.setType("text/html");
                feed.setAlternateLinks(List.of(link));

                feed.setUpdated(new Date());
            }

            @NonNull
            @Override
            protected List<Entry> buildFeedEntries(@NonNull Map<String, Object> model, @NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
                List<ArticleDetail> articleDetailList = getArticleDetailList();
                return articleDetailList.stream().map(articleDetail -> {
                    Entry entry = new Entry();

                    entry.setTitle(getArticleTitle(articleDetail));
                    entry.setUpdated(TimeUtil.convertLocalDateTimeToDate(articleDetail.getArticle().getUpdateTime()));
                    entry.setCreated(TimeUtil.convertLocalDateTimeToDate(articleDetail.getArticle().getCreateTime()));

                    com.rometools.rome.feed.atom.Content content = new com.rometools.rome.feed.atom.Content();
                    content.setType(com.rometools.rome.feed.atom.Content.HTML);
                    content.setValue(getArticleHtmlContent(articleDetail));
                    entry.setContents(List.of(content));

                    Link link = new Link();
                    link.setType("text/html");
                    link.setHref(getArticleUrl(articleDetail));
                    entry.setAlternateLinks(List.of(link));

                    return entry;
                }).collect(Collectors.toList());
            }
        };
    }
}
