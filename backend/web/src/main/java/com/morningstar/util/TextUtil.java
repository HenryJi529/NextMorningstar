package com.morningstar.util;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class TextUtil {
    public static String convertMarkdown2Html(String markdown) {
        // 解析 Markdown
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);

        // 渲染为 HTML 以便提取文本
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
