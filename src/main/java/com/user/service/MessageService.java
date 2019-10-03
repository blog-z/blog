package com.user.service;

import com.dubbo.commons.ServerResponse;
import com.user.entity.Message;
import org.springframework.web.context.request.async.DeferredResult;

public interface MessageService {

    DeferredResult<ServerResponse> setSystemMessages(Message message);
}
