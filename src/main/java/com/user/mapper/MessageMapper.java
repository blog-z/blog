package com.user.mapper;

import com.user.entity.Message;
import java.util.List;

public interface MessageMapper {
    int deleteByPrimaryKey(String messageId);

    int insert(Message record);

    Message selectByPrimaryKey(String messageId);

    List<Message> selectAll();

    int updateByPrimaryKey(Message record);
}