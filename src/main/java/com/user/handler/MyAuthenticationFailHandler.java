package com.user.handler;

import com.dubbo.commons.ServerResponse;
import com.user.utils.ResponseUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException) {
            //可在此记录登录失败次数，进行锁定
            ResponseUtil.out(response, ServerResponse.createByErrorMessage("密码错误"));
        } else if (exception instanceof DisabledException) {
            ResponseUtil.out(response,ServerResponse.createByErrorMessage("账户被禁用，请联系管理员"));
            //可以新增登录异常次数超限LoginFailLimitException
        } else {
            ResponseUtil.out(response,ServerResponse.createByErrorMessage("没有此用户失败"));
        }
    }
}
