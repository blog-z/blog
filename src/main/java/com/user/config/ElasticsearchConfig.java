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

    @Value(value = "spring.data.elasticsearch.cluster-name")
    private String esClusterName;

    @Value(value = "spring.data.elasticsearch.cluster-nodes")
    private String esClusterNodes;


    private static final Logger logger= LoggerFactory.getLogger(ElasticsearchConfig.class);

    @Bean
    public TransportClient MyTransportClient(){
        TransportClient transportClient=null;

        try {
            TransportAddress transportAddress=new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9301);


            Settings elasticsearchSettings=Settings.builder()
                    .put("cluster.name",esClusterName)
                    .build();

            transportClient=new PreBuiltTransportClient(elasticsearchSettings);
            transportClient.addTransportAddress(transportAddress);
        } catch (UnknownHostException e) {
            logger.info("初始化elasticsearch失败！");
        }
        return transportClient;
    }
}
