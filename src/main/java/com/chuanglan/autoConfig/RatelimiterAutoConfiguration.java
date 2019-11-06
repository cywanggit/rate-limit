package com.chuanglan.autoConfig;

import com.chuanglan.config.DefaultRatelimiter;
import com.chuanglan.config.RateLimiter;
import com.chuanglan.entity.MemoryRate;
import com.chuanglan.entity.Rate;
import com.chuanglan.entity.RedisRate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description 自动配置类
 * @FileName RatelimiterAutoConfiguration
 * @auther cw
 * @create 2019-11-01 13:49
 */
@Configuration
@ConfigurationProperties(prefix = "rate")
public class RatelimiterAutoConfiguration {


    private long limitTimeMillis;

    private long time;

    private boolean enableRedis = false;

    private String redisHost = "localhost";

    private int redisPort = 6379;

    private String limitTimeMillisKey;   // 限次时间的 redis key

    private String timeKey; // 限制次数的redis key

    private boolean needRequestTime = false; // 统计请求次数的配置


    public boolean isNeedRequestTime() {
        return needRequestTime;
    }

    public void setNeedRequestTime(boolean needRequestTime) {
        this.needRequestTime = needRequestTime;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
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

    public boolean isEnableRedis() {
        return enableRedis;
    }

    public void setEnableRedis(boolean enableRedis) {
        this.enableRedis = enableRedis;
    }

    public long getLimitTimeMillis() {
        return limitTimeMillis;
    }

    public void setLimitTimeMillis(long limitTimeMillis) {
        this.limitTimeMillis = limitTimeMillis;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    @Bean
    @ConditionalOnMissingBean(Rate.class)
    public Rate rate(){
        if (enableRedis){
            if (limitTimeMillisKey == null || limitTimeMillisKey == "" || timeKey == null || timeKey == ""){
                throw new IllegalArgumentException("limitTimeMillisKey or timeKey is null");
            }
            Rate rate = new RedisRate(redisHost,redisPort,limitTimeMillisKey,timeKey);
            return rate;
        }
        Rate rate = new MemoryRate(limitTimeMillis,time);
        return rate;
    }

    @Bean
    @ConditionalOnBean(Rate.class)
    public RateLimiter rateLimiter(Rate rate){
        RateLimiter rateLimiter = new DefaultRatelimiter(rate);
        rateLimiter.isNeedRequestTime(needRequestTime);
        return rateLimiter;
    }
}
