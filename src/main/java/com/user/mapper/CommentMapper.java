package com.user.mapper;

import com.user.entity.Comment;

import java.util.List;

public interface CommentMapper {
    int deleteByPrimaryKey(String commentId);

    int insert(Comment record);

    Comment selectByPrimaryKey(String commentId);

    List<Comment> selectAll();

    int updateByPrimaryKey(Comment record);
}