package com.user.controller;

import com.dubbo.commons.ServerResponse;
import com.user.entity.Message;
import com.user.service.MessageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/message/")
public class MessageController {

    @Resource(name ="messageService")
    private MessageService messageService;

    @RequestMapping(value = "systemSendMessage",method = RequestMethod.POST)
    public DeferredResult<ServerResponse> sendMessage(Message message){
        return messageService.setSystemMessages(message);
    }
}
