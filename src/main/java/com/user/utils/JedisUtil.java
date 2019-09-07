package com.user.utils;

import com.user.commons.RedisPoolConfig;
import com.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisCluster;

public class JedisUtil {

    //key value 加入redis
    public static void setKey(String key,String value){
        if(key!=null&&value!=null){
            JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
            jedisCluster.set(key,value);
        }
    }

    //key value 加入redis并设置时间keyTime
    public static void setKeyTime(String key,String value,int keyTime){
        JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
        jedisCluster.set(key,value);
        jedisCluster.expire(key,keyTime);
    }


    //将用户的信息在存入redis并设置长时间不操作 超时重新登录
    public static void setLoginTime(String userName,String userEmail){
        if (userName==null){
            JedisUtil.setKeyTime(userEmail+1,"session time",5*60);
        }
        JedisUtil.setKeyTime(userName+1,"session time",5*60);
    }

    //得到用户是否为长时间未操作 有用户返回true 否则返回false
    public static Boolean getLoginTime(String userName,String userEmail){
        if (userName==null){
            return StringUtils.isNotBlank(JedisUtil.getKey(userEmail+1));
        }
        return StringUtils.isNotBlank(JedisUtil.getKey(userName+1));
    }

    //set token 这个token是修改密码和忘记密码时用的
    public static void setToken(String userName,String userEmail,String token){
        if (userName==null){
            JedisUtil.setKeyTime(userEmail+"token",token,60);
        }
        JedisUtil.setKeyTime(userName+"token",token,60);
    }

    //get token 这个token是修改密码和忘记密码时用的
    public static String getToken(String userName,String userEmail){
        JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
        if (userName==null){
            return jedisCluster.get(userEmail+"token");
        }
        return jedisCluster.get(userName+"token");
    }

    //del token
    public static void delToken(String userName,String userEmail){
        JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
        if (userName==null){
            jedisCluster.del(userEmail+"token");
        }else {
            jedisCluster.del(userName+"token");
        }
    }

    //key value 从redis中获取
    private static String getKey(String key){
        if (key!=null){
            JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
            return jedisCluster.get(key);
        }
        return "key为空";
    }

    //从redis中获取用户信息
    public static User getUserFoRedisByUserNameOrUserEmail(String userName, String userEmail){
        JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
        if (userName!=null){
            return JsonUtil.stringToObj(jedisCluster.get(userName), User.class);
        }else {
            return JsonUtil.stringToObj(jedisCluster.get(userEmail), User.class);
        }
    }

    //将用户加入到redis 并永久存在
    public static void setUserToRedis(User user){
        JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
        jedisCluster.set(user.getUserName(), JsonUtil.objToString(user));
    }

    //删除key value
    public static Long delKey(String key){
        if (key==null){
            return 1L;
        }else {
            JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
            return jedisCluster.del(key);
        }
    }

















    public static String getUserId(){
        JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();
        jedisCluster.set("userId",String.valueOf(Integer.parseInt(jedisCluster.get("userId"))+1));
        return String.valueOf(Integer.parseInt(jedisCluster.get("userId"))-1);
    }

}
