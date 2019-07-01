package com.user.mapper;

import com.user.entity.Article;

import java.util.List;

public interface ArticleMapper {
    int deleteByPrimaryKey(String articleId);

    int insert(Article record);

    Article selectByPrimaryKey(String articleId);

    List<Article> selectAll();

    int updateByPrimaryKey(Article record);
}