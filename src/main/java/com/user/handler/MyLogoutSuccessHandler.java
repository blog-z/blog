package com.user.handler;

import com.user.commons.ServerResponse;
import com.user.utils.ResponseUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //清空上下文
        SecurityContextHolder.clearContext();
        // 从session中移除
        request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
        //记录退出日志
        System.out.println("退出登录成功！");
        ResponseUtil.out(response,ServerResponse.createBySuccessMessage("退出登录成功"));
    }
}
