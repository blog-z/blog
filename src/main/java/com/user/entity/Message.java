package com.user.entity;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String messageId;

    private String messageUserId;

    private String messageUserGetId;

    private String messageComment;

    private Date createTime;

    public Message(String messageId, String messageUserId, String messageUserGetId, String messageComment, Date createTime) {
        this.messageId = messageId;
        this.messageUserId = messageUserId;
        this.messageUserGetId = messageUserGetId;
        this.messageComment = messageComment;
        this.createTime = createTime;
    }

    public Message() {
        super();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId == null ? null : messageId.trim();
    }

    public String getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(String messageUserId) {
        this.messageUserId = messageUserId == null ? null : messageUserId.trim();
    }

    public String getMessageUserGetId() {
        return messageUserGetId;
    }

    public void setMessageUserGetId(String messageUserGetId) {
        this.messageUserGetId = messageUserGetId == null ? null : messageUserGetId.trim();
    }

    public String getMessageComment() {
        return messageComment;
    }

    public void setMessageComment(String messageComment) {
        this.messageComment = messageComment == null ? null : messageComment.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}