package com.user.handler;

import com.user.commons.ServerResponse;
import com.user.config.SecurityUserDetails;
import com.user.utils.JwtTokenUtil;
import com.user.utils.ResponseUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        User user=(User) authentication.getPrincipal();
        SecurityUserDetails securityUserDetails=new SecurityUserDetails(user.getUsername(),user.getAuthorities());
        String token=JwtTokenUtil.getToken(securityUserDetails);
        ResponseUtil.out(response,ServerResponse.createBySuccessMessage("登录成功!   accessToken=="+token));
    }
}
