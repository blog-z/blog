package com.user.utils;

import com.dubbo.commons.RedisPoolConfig;
import com.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisCluster;

public class JedisUtil {
    private static JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();

    //将实体json序列化到redis中,redis中存一周时间
    public static <T> void setEntityToRedis(String entityKey, T entity){
        JedisUtil.setKeyTime(entityKey,JsonUtil.objToString(entity),60*60*24*7);
    }


    //key-value加入redis，一级方法
    public static void setKey(String key,String value){
        if(key!=null&&value!=null) {
            jedisCluster.set(key, value);
        }
    }

    //key-value加入redis并设置时间keyTime，一级方法
    private static void setKeyTime(String key, String value, int keyTime){
        jedisCluster.set(key,value);
        jedisCluster.expire(key,keyTime);
    }

    //删除key-value，一级方法
    public static void delKey(String key){
        jedisCluster.del(key);
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
            return StringUtils.isNotBlank(JedisUtil.getValue(userEmail+1));
        }
        return StringUtils.isNotBlank(JedisUtil.getValue(userName+1));

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
        if (userName==null){
            return jedisCluster.get(userEmail+"token");
        }
        return jedisCluster.get(userName+"token");

    }

    //del token
    public static void delToken(String userName,String userEmail){
        if (userName==null){
            jedisCluster.del(userEmail+"token");
        }else {
            jedisCluster.del(userName+"token");
        }
    }

    //key value 从redis中获取
    public static String getValue(String key){
        if (key!=null){
            return jedisCluster.get(key);
        }
        return "key为空";
    }

    //从redis中获取用户信息
    public static User getUserFoRedisByUserNameOrUserEmail(String userName, String userEmail){
        if (userName!=null){
            return JsonUtil.stringToObj(jedisCluster.get(userName), User.class);
        }else {
            return JsonUtil.stringToObj(jedisCluster.get(userEmail), User.class);
        }
    }

    //将用户加入到redis 并永久存在
    public static void setUserToRedis(User user){
        jedisCluster.set(user.getUserName(), JsonUtil.objToString(user));
    }



    //自增ID
    public static String getUserId(){
        jedisCluster.set("userId",String.valueOf(Integer.parseInt(jedisCluster.get("userId"))+1));
        return String.valueOf(Integer.parseInt(jedisCluster.get("userId"))-1);
    }

    //自增ID
    public static String getArticleId(){
        jedisCluster.set("articleId",String.valueOf(Integer.parseInt(jedisCluster.get("articleId"))+1));
        return String.valueOf(Integer.parseInt(jedisCluster.get("articleId"))-1);
    }

    //自增ID
    public static String getCommentId(){
        jedisCluster.set("commentId",String.valueOf(Integer.parseInt(jedisCluster.get("commentId"))+1));
        return String.valueOf(Integer.parseInt(jedisCluster.get("commentId"))-1);
    }

    //自增ID
    public static String getMessageId(){
        jedisCluster.set("messageId",String.valueOf(Integer.parseInt(jedisCluster.get("messageId"))+1));
        return String.valueOf(Integer.parseInt(jedisCluster.get("messageId"))-1);
    }

    //判断是否登录过，登录过--true 没登录或异地登录--false
    public static Boolean isUserLogin(String userNameLogin,String loginUrl){
        String url=jedisCluster.get(userNameLogin);
        return loginUrl.equals(url);
    }

}
