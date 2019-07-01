package com.user.utils;

import redis.clients.jedis.JedisCluster;

public class JedisLock {

    private JedisCluster jedisCluster;

    private String lockKey;

    //锁超时，防止线程在入锁以后，无限的执行等待
    private int expireMsecs=60*1000;

    //锁等待，防止线程饥饿
    private int timeoutMsecs=10*1000;

    private boolean locked = false;

    public JedisLock(JedisCluster jedisCluster,String lockKey){
        this.jedisCluster=jedisCluster;
        this.lockKey=lockKey;
    }

    public JedisLock(JedisCluster jedisCluster,String lockKey,int timeoutMsecs){
        this(jedisCluster, lockKey);
        this.timeoutMsecs=timeoutMsecs;
    }

    public JedisLock(JedisCluster jedisCluster,String lockKey,int timeoutMsecs,int expireMsecs){
        this(jedisCluster, lockKey, timeoutMsecs);
        this.expireMsecs=expireMsecs;
    }

    public JedisLock(String lockKey){
        this(null,lockKey);
    }

    public JedisLock(String lockKey,int timeoutMsecs){
        this(null,lockKey,timeoutMsecs);
    }

    public JedisLock(String lockKey,int timeoutMsecs,int expireMsecs){
        this(null,lockKey,timeoutMsecs,expireMsecs);
    }

    public String getLockKey(){
        return lockKey;
    }

    public boolean acquire() throws InterruptedException{
        return acquire(jedisCluster);
    }


    public boolean acquire(JedisCluster jedisCluster) throws InterruptedException{
        int timeout=timeoutMsecs;
        while (timeout>=0){
            //currentTimeMillis()这个方法得到的是自1970年1月1日零点到目前计算这一刻所经历的的毫秒数，
            // 注意这里返回值为long型，至于为什么不是int，因为int已经存不下了
            //但是这个方法不是非常精准，如果想更精准，使用System.nanoTime()方法，可以达到（纳秒）
            long expires=System.currentTimeMillis()+expireMsecs+1;
            String expiresStr=String.valueOf(expires);  //锁到期时间
            if (jedisCluster.setnx(lockKey,expiresStr)==1){
                locked=true;
                return true;
            }

            String currentValueStr=jedisCluster.get(lockKey);   //redis里的时间
            if (currentValueStr!=null&&Long.parseLong(currentValueStr)<System.currentTimeMillis()){
                //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个判断是过不去的
                String oldValueStr=jedisCluster.getSet(lockKey,expiresStr);
                //获取上一个锁到期时间，并设置现在的锁到期时间
                if (oldValueStr!=null&&oldValueStr.equals(currentValueStr)){
                    //如果这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权力获得锁
                    locked=true;
                    return true;
                }
            }
            timeout-=100;
            Thread.sleep(100);
        }
        return false;
    }

    public void release(){
        release(jedisCluster);
    }

    public void release(JedisCluster jedisCluster){
        if (locked){
            jedisCluster.del(lockKey);
            locked=false;
        }
    }
}






















