package com.user.utils;

import com.dubbo.commons.RedisPoolConfig;
import com.user.entity.User;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JedisUtil {
    //实现读写锁
    private static ReentrantReadWriteLock readWriteLock=new ReentrantReadWriteLock();
    //可重入的共享读锁
    private static Lock readLock=readWriteLock.readLock();
    //可重入的排他写锁
    private static Lock writeLock=readWriteLock.writeLock();

    private static JedisCluster jedisCluster= RedisPoolConfig.getJedisCluster();

    //将实体json序列化到redis中,redis中存一周时间
    public static <T> void setEntityToRedis(String entityKey, T entity){
        JedisUtil.setKeyTime(entityKey,JsonUtil.objToString(entity),60*60*24*7);
    }


    //key-value加入redis，一级方法
    public static void setKey(String key,String value){
        try{
            writeLock.lock();
            if(key!=null&&value!=null){
                jedisCluster.set(key,value);
            }
        }finally {
            writeLock.unlock();
        }

    }

    //key-value加入redis并设置时间keyTime，一级方法
    private static void setKeyTime(String key, String value, int keyTime){
        try{
            writeLock.lock();
            jedisCluster.set(key,value);
            jedisCluster.expire(key,keyTime);
        }finally {
            writeLock.unlock();
        }

    }

    //删除key-value，一级方法
    public static Long delKey(String key){
        try{
            readLock.lock();
            if (key==null){
                return 1L;
            }else {
                return jedisCluster.del(key);
            }
        }finally {
            readLock.unlock();
        }

    }


    //将用户的信息在存入redis并设置长时间不操作 超时重新登录
    public static void setLoginTime(String userName,String userEmail){
        try{
            writeLock.lock();
            if (userName==null){
                JedisUtil.setKeyTime(userEmail+1,"session time",5*60);
            }
            JedisUtil.setKeyTime(userName+1,"session time",5*60);
        }finally {
            writeLock.unlock();
        }

    }

    //得到用户是否为长时间未操作 有用户返回true 否则返回false
    public static Boolean getLoginTime(String userName,String userEmail){
        try{
            readLock.lock();
            if (userName==null){
                return StringUtils.isNotBlank(JedisUtil.getValue(userEmail+1));
            }
            return StringUtils.isNotBlank(JedisUtil.getValue(userName+1));
        }finally {
            readLock.unlock();
        }

    }

    //set token 这个token是修改密码和忘记密码时用的
    public static void setToken(String userName,String userEmail,String token){
        try{
            writeLock.lock();
            if (userName==null){
                JedisUtil.setKeyTime(userEmail+"token",token,60);
            }
            JedisUtil.setKeyTime(userName+"token",token,60);
        }finally {
            writeLock.unlock();
        }

    }

    //get token 这个token是修改密码和忘记密码时用的
    public static String getToken(String userName,String userEmail){
        try{
            readLock.lock();
            if (userName==null){
                return jedisCluster.get(userEmail+"token");
            }
            return jedisCluster.get(userName+"token");
        }finally {
            readLock.unlock();
        }

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



















    public static String getUserId(){
        jedisCluster.set("userId",String.valueOf(Integer.parseInt(jedisCluster.get("userId"))+1));
        return String.valueOf(Integer.parseInt(jedisCluster.get("userId"))-1);
    }

}
