package com.user.mapper;

import com.user.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    int deleteByPrimaryKey(String commentId);

    int insert(Comment record);

    Comment selectByPrimaryKey(String commentId);

    List<Comment> selectAll();

    int updateByPrimaryKey(Comment record);

    List<Comment> selectCommentByArticleId(String articleId);

    List<Comment> selectCommentByFartherId(String fartherId);

    List<Comment> selectCommentByArticleIdAndFartherId(@Param(value = "articleId")String articleId,@Param(value = "fartherId")String fartherId);

}