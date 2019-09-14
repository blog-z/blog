package com.user.config;

import org.apache.http.HttpHost;

import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchConfig {

    private static final Logger logger= LoggerFactory.getLogger(ElasticsearchConfig.class);

    private static RestClient lowRestClient=null;

    static {
        init();
    }

    private static void init(){
        RestClientBuilder restClientBuilder=RestClient.builder(new HttpHost("localhost",9201,"http"));
        restClientBuilder.setRequestConfigCallback(builder -> {
            builder.setConnectTimeout(10000);
            builder.setSocketTimeout(20000);
            builder.setConnectionRequestTimeout(30000);
            return builder;
        });

        restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom()
                .setIoThreadCount(100)  //线程数配置
                .setConnectTimeout(10000)
                .setSoTimeout(10000)
                .build()));

        //设置超时
        restClientBuilder.setMaxRetryTimeoutMillis(10000);
        //构建low level client
        lowRestClient=restClientBuilder.build();
        //构建high level client
        logger.info("-------------------------elasticsearch 的 REST Client 初始化完成---------------");
    }

    public static RestClient getLowRestClient(){
        return lowRestClient;
    }
}
