package com.user.service;

import com.dubbo.commons.ServerResponse;
import com.user.entity.Comment;


public interface CommentService {

    ServerResponse addComment(Comment comment);

    ServerResponse deleteComment(String commentId);

    ServerResponse updateComment(String commentId,String commentContent);

    ServerResponse selectComment(String articleId);

    void deleteCommentsForDeleteArticle(String articleId);

    ServerResponse getOwnComments(String commentUserId);
}
