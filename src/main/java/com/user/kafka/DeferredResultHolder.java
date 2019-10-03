package com.user.kafka;

import com.dubbo.commons.ServerResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeferredResultHolder {

    private Map<String, DeferredResult<ServerResponse>> map=new HashMap<>();

    public Map<String, DeferredResult<ServerResponse>> getMap() {
        return map;
    }

    public void setMap(Map<String, DeferredResult<ServerResponse>> map) {
        this.map = map;
    }
}
