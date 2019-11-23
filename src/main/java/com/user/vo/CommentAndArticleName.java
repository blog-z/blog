package com.user.vo;

import java.sql.Date;

public class CommentAndArticleName {

    private String articleTitle;

    private String commentId;

    private String commentUserId;

    private String commentArticleId;

    private String commentFarther;

    private String commentContent;

    private java.sql.Date createTime;

    private java.sql.Date updateTime;

    public CommentAndArticleName(String articleTitle,String commentId, String commentUserId, String commentArticleId, String commentFarther, String commentContent, Date createTime, Date updateTime) {
        this.articleTitle=articleTitle;
        this.commentId = commentId;
        this.commentUserId = commentUserId;
        this.commentArticleId = commentArticleId;
        this.commentFarther = commentFarther;
        this.commentContent = commentContent;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentArticleId() {
        return commentArticleId;
    }

    public void setCommentArticleId(String commentArticleId) {
        this.commentArticleId = commentArticleId;
    }

    public String getCommentFarther() {
        return commentFarther;
    }

    public void setCommentFarther(String commentFarther) {
        this.commentFarther = commentFarther;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
