package com.user.service.impl;

import com.user.commons.Const;
import com.user.commons.ServerResponse;
import com.user.entity.User;
import com.user.mapper.UserMapper;
import com.user.service.UserService;
import com.user.utils.JedisUtil;
import com.user.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    //登录
    public ServerResponse login(String userName, String userEmail, String userPassword){
        User user= JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,userEmail);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        if (!bCryptPasswordEncoder.matches(userPassword,userMapper.selectPasswordByUserNameForSecurity(userName))){
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }
        return ServerResponse.createBySuccess("登录成功！",user);
    }

    //注册
    public ServerResponse<String> register(User user){
        int count=userMapper.selectByUserNameAndEmail(user.getUserName(),user.getUserEmail());
        if (count!=0){
            logger.info("===============负载到这了tomcat===============");
            return ServerResponse.createByErrorMessage("用户名或email已使用");
        }
        user.setUserId(JedisUtil.getUserId());
        user.setUserRole(Const.Role.ROLE_CONSUMER);
        user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));
        int rowCount=userMapper.insert(user);
        if (rowCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        setUserRedis(user);
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    //得到用户详细信息
    public ServerResponse getUserMessage(String userName, String userEmail){
        User user= JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,userEmail);
        return ServerResponse.createBySuccess("spring security",user);
    }

    private void setUserRedis(User user){
        JedisUtil.setKey(user.getUserName(), JsonUtil.objToString(user));
        JedisUtil.setKey(user.getUserEmail(), JsonUtil.objToString(user));
    }


    //忘记密码
    public ServerResponse getQuestion(String userName, String userPassword){
        User user= JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,userPassword);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        return ServerResponse.createBySuccessMessage(user.getUserQuestion());
    }

    //回答忘记密码的问题，得到token
    public ServerResponse setAnswer(String userName, String userEmail, String answer){
        if (!checkUser(userName,userEmail).isSuccess()){
            return ServerResponse.createByErrorMessage("用户已存在");
        }
        if (JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,userEmail).getUserAnswer().equals(answer)){
            String token= UUID.randomUUID().toString();
            JedisUtil.setToken(userName,userEmail,token);
            return ServerResponse.createBySuccessMessage(token);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    //修改密码
    public ServerResponse setPassword(String userName, String userEmail, String password){
        User user=userMapper.selectByUserNameOrEmail(userName,userEmail);
        user.setUserPassword(password);
        int count=userMapper.updateByPrimaryKey(user);
        if (count==1){
            JedisUtil.setUserToRedis(user);
            //删除redis中的token数据，防止不需要重新获取token就能修改
            JedisUtil.delToken(userName,userEmail);
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createBySuccessMessage("修改密码失败");
    }













    private ServerResponse checkUser(String userName, String userEmail){
        if (!StringUtils.isNotBlank(JsonUtil.objToString(JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,userEmail)))){
            if (userName!=null){
                return ServerResponse.createByErrorMessage("用户名已存在！");
            }
            return ServerResponse.createByErrorMessage("email已存在！");
        }
        return ServerResponse.createBySuccessMessage("符合要求！");
    }

}













