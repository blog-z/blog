package com.user.controller;

import com.user.commons.ServerResponse;
import com.user.entity.User;
import com.user.service.UserService;
import com.user.utils.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/user/")
public class UserController {

    @Autowired
    private UserService userService;

    //登录
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public ServerResponse login(String userName, String userEmail, String userPassword){
        ServerResponse response=userService.login(userName,userEmail,userPassword);
        if (response.isSuccess()){
            JedisUtil.setLoginTime(userName,userEmail);
        }
        return response;
    }

    //注册
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ServerResponse<String> register(User user){
        JedisUtil.setKeyTime(user.getUserName()+1,"session time",2*60);
        JedisUtil.setKeyTime(user.getUserEmail()+1,"session time",2*60);
        return userService.register(user);
    }

    //得到用户详细信息
    @RequestMapping(value = "getUserMessage",method = RequestMethod.POST)
    public ServerResponse getUserMessage(String userName,String userEmail){
        return userService.getUserMessage(userName,userEmail);
    }

    //忘记密码，得到忘记密码问题
    @RequestMapping(value = "getQuestion",method = RequestMethod.POST)
    public ServerResponse getQuestion(String userName, String userEmail){
        if (!JedisUtil.getLoginTime(userName,userEmail)){
            return ServerResponse.createByErrorMessage("请登录！");
        }
        return userService.getQuestion(userName,userEmail);
    }

    //输入忘记密码的问题答案   并给一个token 存入redis并有120秒时间限制
    @RequestMapping(value = "set_answer",method = RequestMethod.POST)
    public ServerResponse setAnswer(String userName, String userEmail, String answer, HttpSession session){
        if (!JedisUtil.getLoginTime(userName,userEmail)){
            return ServerResponse.createByErrorMessage("请登录！");
        }
        ServerResponse response=userService.setAnswer(userName,userEmail,answer);
        session.setAttribute("token",response.getMsg());
        return response;
    }

    //输入新密码
    @RequestMapping(value = "set_password",method = RequestMethod.POST)
    public ServerResponse setPassword(String userName, String userEmail, String password, String token){
        if (!JedisUtil.getLoginTime(userName,userEmail)){
            return ServerResponse.createByErrorMessage("请登录！");
        }
        if (!token.equals(JedisUtil.getToken(userName,userEmail))){
            return ServerResponse.createByErrorMessage("请重新操作");
        }
        return userService.setPassword(userName,userEmail,password);
    }
}














