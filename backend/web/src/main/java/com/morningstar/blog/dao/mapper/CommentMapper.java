package com.morningstar.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morningstar.blog.pojo.bo.CommentDetail;
import com.morningstar.blog.pojo.po.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> selectRandomN(int N);

    List<Comment> selectRandomNByArticleId(@Param("articleId") long articleId, @Param("N") int N);

    List<CommentDetail> selectCommentDetailByArticleId(@Param("articleId") long articleId, @Param("limit") int limit, @Param("offset") int offset);
}
