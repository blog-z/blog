package com.user.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticsearchConfig {

    private static final Logger logger= LoggerFactory.getLogger(ElasticsearchConfig.class);

    @Bean(name = "myTransportClient")
    public TransportClient MyTransportClient(){
        logger.info("==========初始化elasticsearch！========");
        TransportClient transportClient=null;
        try {
            TransportAddress transportAddress1=new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9301);
            TransportAddress transportAddress2=new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9302);
            TransportAddress transportAddress3=new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9303);


            Settings elasticsearchSettings=Settings.builder()
                    .put("cluster.name","qingchun-es-cluster")
//                    .put("client.transport.sniff","true")
                    .build();

            transportClient=new PreBuiltTransportClient(elasticsearchSettings);
            transportClient.addTransportAddress(transportAddress1);
            transportClient.addTransportAddress(transportAddress2);
            transportClient.addTransportAddress(transportAddress3);
            logger.info("初始化elasticsearch！");

        } catch (UnknownHostException e) {
            logger.info("初始化elasticsearch失败！");
        }
        return transportClient;
    }
}
