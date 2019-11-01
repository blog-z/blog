package com.user.controller;

import com.dubbo.commons.ServerResponse;
import com.user.entity.User;
import com.user.service.UserService;
import com.user.utils.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin(allowedHeaders="*", allowCredentials="false", methods={RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
@RestController
@RequestMapping(value = "/user/")
public class UserController {

    @Autowired
    private UserService userService;

    //登录
    @CrossOrigin(allowedHeaders="*", allowCredentials="false", methods={RequestMethod.GET,RequestMethod.POST,RequestMethod.OPTIONS})
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public ServerResponse login(@RequestParam(value = "username") String userName, @RequestParam(required = false) String userEmail, @RequestParam(value = "password") String userPassword){
        return userService.login(userName,userEmail,userPassword);
    }

    //注册
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ServerResponse<String> register(User user){
        return userService.register(user);
    }

    //注册检测用户名
    @RequestMapping(value = "checkRegisterUserName",method = RequestMethod.POST)
    public ServerResponse<String> checkRegisterUserName(String userName){
        return userService.checkRegisterUserNameOrEmail(userName);
    }

    //注册检查邮箱
    @RequestMapping(value = "checkRegisterUserEmail",method = RequestMethod.POST)
    public ServerResponse<String> checkRegisterUserEmail(String userEmail){
        return userService.checkRegisterUserNameOrEmail(userEmail);
    }

    //得到用户详细信息
    @RequestMapping(value = "getUserMessage",method = RequestMethod.POST)
    public ServerResponse getUserMessage(HttpServletRequest httpServletRequest,String userName, String userEmail){
        return userService.getUserMessage(userName,userEmail);
    }

    //忘记密码，得到忘记密码问题
    @RequestMapping(value = "getQuestion",method = RequestMethod.POST)
    public ServerResponse getQuestion(String userName, String userEmail){
        return userService.getQuestion(userName,userEmail);
    }

    //忘记密码，通过email等到验证数字
    @RequestMapping(value = "getEmailCheck",method = RequestMethod.POST)
    public ServerResponse getEmailCheck(String userEmail){
        return userService.getEmailNumber(userEmail);
    }

    //忘记密码，通过email
    @RequestMapping(value = "setPasswordByEmail",method = RequestMethod.POST)
    public ServerResponse setPasswordByEmail(String userEmail,String passwordNumber,String password){
        return userService.EmailSetPassword(userEmail,passwordNumber,password);
    }

    //输入忘记密码的问题答案   并给一个token 存入redis并有60秒时间限制
    @RequestMapping(value = "setAnswer",method = RequestMethod.POST)
    public ServerResponse setAnswer(String userName, String userEmail, String userAnswer){
        return userService.setAnswer(userName,userEmail,userAnswer);
    }

    //输入新密码
    @RequestMapping(value = "setPassword",method = RequestMethod.POST)
    public ServerResponse setPassword(String userName, String userEmail, String password, String token){
        if (!token.equals(JedisUtil.getToken(userName,userEmail))){
            return ServerResponse.createByErrorMessage("操作时间超时，请重新操作");
        }
        return userService.setPassword(userName,userEmail,password);
    }





}














