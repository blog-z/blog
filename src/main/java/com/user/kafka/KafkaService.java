package com.user.kafka;

import com.dubbo.commons.ServerResponse;
import com.dubbo.util.JsonUtil;
import com.user.entity.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaService {

    private static final Logger logger= LoggerFactory.getLogger(KafkaService.class);

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    @KafkaListener(topics = "blogGet",groupId = "blogProducerGroup")
    public void listenKafka(ConsumerRecord<String,String> consumerRecord){
        new Thread(() -> {
            //监听kafka topic blogSend
            String key=consumerRecord.key();
            String value=consumerRecord.value();
            String topic=consumerRecord.topic();
            Message message= JsonUtil.stringToObj(value,Message.class);
            logger.info("key:"+key);
            logger.info("value:"+value);
            logger.info("topic:"+topic);
            deferredResultHolder.getMap().get(message.getMessageId()).setResult(ServerResponse.createBySuccessMessage("成功处理"));
        }).start();
    }
}
