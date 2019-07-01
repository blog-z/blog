package com.user.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class RedisPoolConfig {

    private static final Logger logger= LoggerFactory.getLogger(RedisPoolConfig.class);

    private static JedisCluster jedisCluster;

//    @Value("${spring.redis.cluster.nodes:#{'127.0.0.1:6381,127.0.0.1:6382,127.0.0.1:6383,127.0.0.1:6384,127.0.0.1:6385,127.0.0.1:6386'}}")
    private static String clusterNodes="127.0.0.1:6381,127.0.0.1:6382,127.0.0.1:6383,127.0.0.1:6384,127.0.0.1:6385,127.0.0.1:6386";

    @Value("${spring.redis.pool.max-idle:#{'8'}")
    private static int maxIdle=8;

    @Value("${spring.redis.pool.max-wait:#{'-1ms'}")
    private static long maxWaitMillis=-1;


    public static void initPool(){
        String[] nodes=clusterNodes.split(",");
        /*nodes
         * 127.0.0.1:6381
         * 127.0.0.1:6382
         * 127.0.0.1:6383
         * 127.0.0.1:6384
         * 127.0.0.1:6385
         * 127.0.0.1:6386
         * */
        Set<HostAndPort> hostAndPorts=new HashSet<>();

        for (String node : nodes){
            String[] hostAndPort=node.split(":");
            hostAndPorts.add(new HostAndPort(hostAndPort[0],Integer.parseInt(hostAndPort[1])));
        }
        /*hostAndPorts
         * 127.0.0.1 6381
         * 127.0.0.1 6382
         * 127.0.0.1 6383
         * 127.0.0.1 6384
         * 127.0.0.1 6385
         * 127.0.0.1 6386
         * */

        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

        jedisCluster=new JedisCluster(hostAndPorts,5000,jedisPoolConfig);
        logger.info("===============================new a pojo==================================");
    }

    static {
        initPool();
    }

    public static JedisCluster getJedisCluster(){
        return jedisCluster;
    }



}
