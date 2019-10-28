package com.user.service.impl;

import com.dubbo.commons.ServerResponse;
import com.user.entity.Message;
import com.user.kafka.DeferredResultHolder;
import com.user.service.MessageService;
import com.user.utils.JedisUtil;
import com.user.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;


@Service(value = "messageService")
public class MessageServiceImpl implements MessageService {

    private static final Logger logger= LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    /**
     * 发送消息
     * provide发送消息到kafka中topic(sendMessage)
     * consumer订阅topic(sendMessage)中的消息，然后将消息处理，并最后把处理结果发送到topic(getMessage)中
     */
    @Async
    public DeferredResult<ServerResponse> setSystemMessages(Message message){
        logger.info("主线程开始");
        message.setMessageId(JedisUtil.getMessageId());
        kafkaTemplate.send("blogSend",message.getMessageId(),JsonUtil.objToString(message));
        DeferredResult<ServerResponse> deferredResult=new DeferredResult<>();
        deferredResultHolder.getMap().put(message.getMessageId(),deferredResult);
        logger.info("主线程结束");
        return deferredResult;
    }
}
