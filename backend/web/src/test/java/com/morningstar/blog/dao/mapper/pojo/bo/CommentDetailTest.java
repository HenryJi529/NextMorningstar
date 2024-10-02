package com.morningstar.blog.dao.mapper.pojo.bo;

import com.morningstar.blog.pojo.bo.CommentDetail;
import com.morningstar.blog.pojo.po.Comment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;

@Slf4j
public class CommentDetailTest {
    @Test
    public void test() {
        Comment comment = Comment
                .builder()
                .id(123L)
                .articleId(1234L)
                .build();
        CommentDetail commentDetail = new CommentDetail("user1", "小白", "1.png", new HashSet<>(), new HashSet<>());
        BeanUtils.copyProperties(comment, commentDetail);
        System.out.println(commentDetail);
    }
}
