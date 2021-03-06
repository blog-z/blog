package com.user.entity;

import java.util.Date;

public class Comment {
    private String commentId;

    private String commentUserId;

    private String commentArticleId;

    private String commentFarther;

    private String commentContent;

    private Date createTime;

    private Date updateTime;

    public Comment(String commentId, String commentUserId, String commentArticleId, String commentFarther, String commentContent, Date createTime, Date updateTime) {
        this.commentId = commentId;
        this.commentUserId = commentUserId;
        this.commentArticleId = commentArticleId;
        this.commentFarther = commentFarther;
        this.commentContent = commentContent;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Comment() {
        super();
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId == null ? null : commentId.trim();
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId == null ? null : commentUserId.trim();
    }

    public String getCommentArticleId() {
        return commentArticleId;
    }

    public void setCommentArticleId(String commentArticleId) {
        this.commentArticleId = commentArticleId == null ? null : commentArticleId.trim();
    }

    public String getCommentFarther() {
        return commentFarther;
    }

    public void setCommentFarther(String commentFarther) {
        this.commentFarther = commentFarther == null ? null : commentFarther.trim();
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent == null ? null : commentContent.trim();
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