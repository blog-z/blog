package com.user.commons;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitmqConfig {

    private static ConnectionFactory factory;

    private static Connection connection;

    private static Channel channel;

    static {
        factory.setUsername("zhuqingchun");
        factory.setPassword("729512117");
        factory.setVirtualHost("/");
        factory.setHost("localhost");
        factory.setPort(8080);
        try {
            connection=factory.newConnection();
            channel=connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static Channel getChannel(){
        return channel;
    }

}
