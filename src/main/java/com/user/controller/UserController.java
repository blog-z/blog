package com.user.controller;

import com.dubbo.commons.ServerResponse;
import com.user.entity.User;
import com.user.service.UserService;
import com.user.utils.JedisUtil;
import com.user.utils.JwtTokenUtil;
import com.user.utils.ResponseUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/user/")
public class UserController {

    @Autowired
    private UserService userService;

    //登录
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public ServerResponse login(@RequestParam(value = "username") String userName, @RequestParam(required = false) String userEmail, @RequestParam(value = "password") String userPassword){
        return userService.login(userName,userEmail,userPassword);
    }

    //注册
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ServerResponse<String> register(User user){
        return userService.register(user);
    }

    //得到用户详细信息
    @RequestMapping(value = "getUserMessage",method = RequestMethod.POST)
    public ServerResponse getUserMessage(HttpServletRequest httpServletRequest,String userName, String userEmail){
        if (checkRole(httpServletRequest,userName)){
            return userService.getUserMessage(userName,userEmail);
        }
        return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
    }

    //忘记密码，得到忘记密码问题
    @RequestMapping(value = "getQuestion",method = RequestMethod.POST)
    public ServerResponse getQuestion(HttpServletRequest httpServletRequest,String userName, String userEmail){
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        return userService.getQuestion(userName,userEmail);
    }

    //输入忘记密码的问题答案   并给一个token 存入redis并有60秒时间限制
    @RequestMapping(value = "setAnswer",method = RequestMethod.POST)
    public ServerResponse setAnswer(HttpServletRequest httpServletRequest,String userName, String userEmail, String answer){
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        return userService.setAnswer(userName,userEmail,answer);
    }

    //输入新密码
    @RequestMapping(value = "setPassword",method = RequestMethod.POST)
    public ServerResponse setPassword(HttpServletRequest httpServletRequest,String userName, String userEmail, String password, String token){
        if (!checkRole(httpServletRequest, userName)){
            return ServerResponse.createByErrorMessage("你使用的用户名和jwt token不一致");
        }
        if (!token.equals(JedisUtil.getToken(userName,userEmail))){
            return ServerResponse.createByErrorMessage("操作时间超时，请重新操作");
        }
        return userService.setPassword(userName,userEmail,password);
    }




    private Boolean checkRole(HttpServletRequest httpServletRequest,String userName){
        String token = httpServletRequest.getHeader(JwtTokenUtil.tokenHeader);
        if (!StringUtils.isEmpty(token)) {
            Claims claims = Jwts.parser()
                    .setSigningKey(JwtTokenUtil.secret)
                    .parseClaimsJws(token.replace(JwtTokenUtil.tokenPrefix, ""))
                    .getBody();
            //相等jwt中token和用户名一致
            if (claims.getSubject().equals(userName)){
                return true;
            }
        }
        return false;
    }
}














