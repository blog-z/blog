package com.user.elasticsearch.controller;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ElasticsearchUserController {

    @Autowired
    @Qualifier(value = "MyTransportClient")
    private TransportClient transportClient;


}
