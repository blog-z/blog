package com.user.service.impl;

import com.dubbo.commons.Const;
import com.dubbo.commons.ServerResponse;
import com.user.entity.User;
import com.user.kafka.DeferredResultHolder;
import com.user.mapper.UserMapper;
import com.user.service.UserService;
import com.user.utils.JedisUtil;
import com.user.utils.JsonUtil;
import com.user.utils.RabbitmqUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


@Service("userService")
public class UserServiceImpl implements UserService {

    private final static Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

//    @Autowired
//    private DeferredResultHolder deferredResultHolder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;


    //登录
    public ServerResponse login(String userName, String userEmail, String userPassword){
        if (userName!=null&&!bCryptPasswordEncoder.matches(userPassword,userMapper.selectPasswordByUserNameForSecurity(userName))){
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }
        if (userEmail!=null&&!bCryptPasswordEncoder.matches(userPassword,userMapper.selectByUserEmail(userEmail).getUserPassword())){
            return ServerResponse.createByErrorMessage("用户名或密码错误");
        }
        return ServerResponse.createBySuccessMessage("登录成功");
    }

    //注册检查userName
    public ServerResponse<String> checkRegisterUserNameOrEmail(String userNameOrEmail){
        if (userMapper.checkUserNameOrEmail(userNameOrEmail,userNameOrEmail)>0){
            //如果redis中有此用户名，则不能在使用此用户名了
            return ServerResponse.createByErrorMessage("以存在");
        }
        return ServerResponse.createBySuccessMessage("不存在");
    }

    //注册
    public ServerResponse<String> register(User user){
        String response="";
        int countUserName=userMapper.selectByUserNameOrEmailOrPhone(user.getUserName(),null,null);
        int countUserEmail=userMapper.selectByUserNameOrEmailOrPhone(null,user.getUserEmail(),null);
//        int countPhone=userMapper.selectByUserNameOrEmailOrPhone(null,null,user.getUserPhone());
        if (countUserName+countUserEmail!=0){
            if (countUserName!=0){
                response+="用户名";
            }
            if (countUserEmail!=0) {
                response+="邮箱";
            }
//            if (countPhone != 0) {
//                response+="电话号码";
//            }
            return ServerResponse.createByErrorMessage(response+"已存在");
        }

        user.setUserId(JedisUtil.getUserId());
        user.setUserRole(Const.Role.ROLE_CONSUMER);
        user.setUserPassword(bCryptPasswordEncoder.encode(user.getUserPassword()));
        //TODO: 20/10/2019 目前不用rabbitmq
        /**
         *  RabbitmqUtil.rabbitmqRegisterUser(user);
         *  DeferredResult<ServerResponse> deferredResult=new DeferredResult<>();
         *  deferredResultHolder.getMap().put(user.getUserId(),deferredResult);
         */
        int rowCount=userMapper.insert(user);
        if (rowCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        //注册成功后将用户信息存入redis中
        setUserRedis(userMapper.selectByUserNameOrEmail(user.getUserName(),user.getUserEmail()));
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    //得到用户详细信息，直接从redis中拿，但将密码设为空
    public ServerResponse getUserMessage(String userName, String userEmail){
        User user= JedisUtil.getUserFoRedisByUserNameOrUserEmail(userName,userEmail);
        user.setUserPassword("空");
        user.setUserAnswer("空");
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
        user.setUserPassword(bCryptPasswordEncoder.encode(password));
        int count=userMapper.updateByPrimaryKey(user);
        if (count==1){
            JedisUtil.setUserToRedis(user);
            //删除redis中的token数据，防止不需要重新获取token就能修改
            JedisUtil.delToken(userName,userEmail);
            return ServerResponse.createBySuccessMessage("修改密码成功");
        }
        return ServerResponse.createBySuccessMessage("修改密码失败");
    }

    //忘记密码，通过email等到验证数字
    public ServerResponse getEmailNumber(String userEmail){
        User user= JsonUtil.stringToObj(JedisUtil.getValue(userEmail),User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage("此Email未注册");
        }
        Random random=new Random();
        String passwordNumber=String.valueOf(random.nextInt(999999)+100000);
        SimpleMailMessage message = new SimpleMailMessage();
        if(userEmail != null) {
            try {
                message.setTo(userEmail);//收信人
                message.setSubject("校园论坛");//主题
                message.setText("您的忘记密码的验证码(只有60秒有效)："+passwordNumber);//内容
                message.setFrom(from);//发信人
                javaMailSender.send(message);
            } catch (Exception e) {
                logger.info("错误"+e);
            }
            JedisUtil.setToken(null,userEmail,passwordNumber);
            return ServerResponse.createBySuccessMessage("发送email成功");
        }else {
            return ServerResponse.createByErrorMessage("email不能为空");
        }
    }

    //修改密码，通过email
    public ServerResponse EmailSetPassword(String userEmail,String passwordNumber,String password){
        User user=userMapper.selectByUserEmail(userEmail);
        user.setUserPassword(bCryptPasswordEncoder.encode(password));
        if (JedisUtil.getToken(null,userEmail)==null){
            return ServerResponse.createByErrorMessage("超时，请重新发送");
        }
        if (passwordNumber!=null&&!passwordNumber.equals(JedisUtil.getToken(null,userEmail))){
            return ServerResponse.createByErrorMessage("验证码错误");
        }
        int count=userMapper.updateByPrimaryKey(user);
        if (count==1){
            JedisUtil.setUserToRedis(user);
            //删除redis中的token数据，防止不需要重新获取token就能修改
            JedisUtil.delToken(null,userEmail);
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













