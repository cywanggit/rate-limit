package com.chuanglan.entity;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description 基于Redis进行限流（适用于分布式集群）
 * @FileName RedisRate
 * @auther cw
 * @create 2019-11-01 13:43
 */
public class RedisRate implements Rate{

    JedisPool jedisPool;

    private long limitTimeMillis;

    private long time;

    private String limitTimeMillisKey;

    private String timeKey;


    public RedisRate(String host, int port,String limitTimeMillisKey,String timeKey) {
        //1 获得连接池配置对象，设置配置项
        JedisPoolConfig config = new JedisPoolConfig();
        // 1.1 最大连接数
        config.setMaxTotal(30);
        //1.2 最大空闲连接数
        config.setMaxIdle(10);
        //获得连接池
        jedisPool = new JedisPool(config,host,port);
        Jedis resource = jedisPool.getResource();
        this.limitTimeMillisKey = limitTimeMillisKey;
        this.timeKey = timeKey;
        limitTimeMillis = Long.valueOf(resource.get(limitTimeMillisKey));
        time = Long.valueOf(resource.get(timeKey));
        resource.close();

    }

    public String getLimitTimeMillisKey() {
        return limitTimeMillisKey;
    }

    public void setLimitTimeMillisKey(String limitTimeMillisKey) {
        this.limitTimeMillisKey = limitTimeMillisKey;
    }

    public String getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }

    public Jedis getClient(){
        return jedisPool.getResource();
    }

    public void closeJedis(Jedis client){
        client.close();
    }

    @Override
    public long getLimitTimeMillis() {

        return limitTimeMillis;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public long getStartLimitTimeMillis() {
        Jedis jedis = jedisPool.getResource();
        String default_startLimitTimeMillis = jedis.get("default_startLimitTimeMillis");
        jedis.close();
        return Long.valueOf(default_startLimitTimeMillis);
    }



    @Override
    public void setStartLimitTimeMillis(long startLimitTimeMillis) {
        Jedis jedis = jedisPool.getResource();
        jedis.set("default_startLimitTimeMillis",String.valueOf(startLimitTimeMillis));
        jedis.close();
    }

    @Override
    public long getCurrentTime() {
        Jedis jedis = jedisPool.getResource();
        Long default_currentTime = Long.valueOf(jedis.get("default_currentTime"));
        jedis.close();
        return default_currentTime;
    }

    @Override
    public void setCurrentTime(long currentTime) {
        Jedis jedis = jedisPool.getResource();
        jedis.set("default_currentTime",String.valueOf(currentTime));
        jedis.close();
    }

    @Override
    public void addTime() {
        Jedis jedis = jedisPool.getResource();
        jedis.incr("default_currentTime");
        jedis.close();
    }



    public boolean getLock(){
        Jedis jedis = jedisPool.getResource();
        Long lock = jedis.setnx("rate_lock", "1");
        boolean flag = lock == 1;
        jedis.expire("rate_lock",(int)limitTimeMillis/1000);
        jedis.close();
        return flag;

    }

    public void releaseLock(){
        Jedis jedis = jedisPool.getResource();
//        jedis.watch("rate_lock");
//        Transaction multi = jedis.multi();
//        multi.del("rate_lock");
//        multi.exec();
//        jedis.unwatch();
        jedis.del("rate_lock");

        jedis.close();
    }


}
