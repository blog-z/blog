package com.user.controller;

import com.user.commons.ServerResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("/authentication/")
@RestController
public class BrowserSecurityController {

    private Logger logger= LoggerFactory.getLogger(BrowserSecurityController.class);

    private RequestCache requestCache=new HttpSessionRequestCache();

    //跳转工具类
    private RedirectStrategy redirectStrategy=new DefaultRedirectStrategy();

    @RequestMapping("require")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ServerResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {

        SavedRequest savedRequest=requestCache.getRequest(request,response);
        if (savedRequest!=null){
            String targetUrl=savedRequest.getRedirectUrl();    //得到引发跳转的url
            logger.info("引发跳转的url是:"+targetUrl);
            if (StringUtils.endsWithIgnoreCase(targetUrl,".html")){
                redirectStrategy.sendRedirect(request,response,"");
            }
        }
        return ServerResponse.createByErrorMessage("无权限，请登陆!!!");
    }
}
