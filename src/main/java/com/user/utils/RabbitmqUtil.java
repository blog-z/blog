package com.user.utils;

import com.dubbo.commons.RabbitmqConfig;
import com.dubbo.util.JsonUtil;
import com.rabbitmq.client.BuiltinExchangeType;
import com.user.entity.User;
import com.user.kafka.DeferredResultHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RabbitmqUtil {

    private final static Logger logger= LoggerFactory.getLogger(RabbitmqUtil.class);

    public void setMessage(Byte[] message) throws Exception{

        RabbitmqConfig.getChannel().exchangeDeclare("exchangeName","direct",true);

        RabbitmqConfig.getChannel().queueDeclare("queueName",true,false,false,null);

        RabbitmqConfig.getChannel().queueBind("exchangeName","queueName","rounting");
    }

    /**
     * 在注册用户是使用rabbitmq
     */
    public static void rabbitmqRegisterUser(User user){
        try {
            //声明直连交换机
            RabbitmqConfig.getChannel().exchangeDeclare("exchangeRegisterUser", BuiltinExchangeType.DIRECT);
            //声明持久化，不排他，不自动删除队列
            RabbitmqConfig.getChannel().queueDeclare("queueRegisterUser",true,false,false,null);
            //绑定交换机和队列
            RabbitmqConfig.getChannel().queueBind("registerUser","queueRegisterUser","qingchun");
            //将消息发送到队列中
            RabbitmqConfig.getChannel().basicPublish(
                    "exchangeRegisterUser",
                    "qingchun",
                    null,
                    JsonUtil.objToString(user).getBytes()
            );
        } catch (IOException e) {
            logger.warn("发送到rabbitmq出现问题"+e);
        }
    }
}
