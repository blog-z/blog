package com.user.vo;

import com.user.entity.Comment;

import java.util.Date;
import java.util.List;

public class CommentVo {

    public CommentVo(List<CommentVo> commentVoList, String commentId, String commentUserId, String commentArticleId, String commentFarther, String commentContent, Date createTime, Date updateTime) {
        this.commentId = commentId;
        this.commentUserId = commentUserId;
        this.commentArticleId = commentArticleId;
        this.commentFarther = commentFarther;
        this.commentContent = commentContent;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.commentVoList = commentVoList;
    }

    private String commentId;

    private String commentUserId;

    private String commentArticleId;

    private String commentFarther;

    private String commentContent;

    private Date createTime;

    private Date updateTime;

    private List<CommentVo> commentVoList;

    public List<CommentVo> getCommentVoList() {
        return commentVoList;
    }

    public void setCommentVoList(List<CommentVo> commentVoList) {
        this.commentVoList = commentVoList;
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
