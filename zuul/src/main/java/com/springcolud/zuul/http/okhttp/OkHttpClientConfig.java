package com.springcolud.zuul.http.okhttp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @program: rcs-commonkit
 * @description: okhttp客户端配置类
 * @author: WangJinBo
 * @create: 2019-12-12 15:02
 **/
@Configuration
public class OkHttpClientConfig {

    @Value("${okhttp.followSslRedirects}")
    private boolean followSslRedirects = true;

    @Value("${okhttp.followRedirects}")
    private boolean followRedirects = true;

    @Value("${okhttp.retryOnConnectionFailure}")
    private boolean retryOnConnectionFailure = false;

    @Value("${okhttp.connectTimeout}")
    private int connectTimeout = 5000;

    @Value("${okhttp.readTimeout}")
    private int readTimeout = 20000;

    @Value("${okhttp.writeTimeout}")
    private int writeTimeout = 3000;

    @Value("${okhttp.pingInterval}")
    private int pingInterval = 10 * 1000;

    @Value("${okhttp.maxIdleConnections}")
    private int maxIdleConnections = 50;

    @Value("${okhttp.keepAliveDuration}")
    private int keepAliveDuration = 10 * 60 * 1000;

    @Value("${okhttp.cacheAble}")
    private String cacheType=OkHttpCacheType.NOSTORE;

    @Value("${okhttp.maxAge}")
    private Integer maxAge=3600000;

    @Value("${okhttp.cachePath}")
    private String cachePath;

    public OkHttpClientConfig(){}
    public boolean isFollowSslRedirects() {
        return followSslRedirects;
    }

    public void setFollowSslRedirects(boolean followSslRedirects) {
        this.followSslRedirects = followSslRedirects;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public boolean isRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public void setRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getPingInterval() {
        return pingInterval;
    }

    public void setPingInterval(int pingInterval) {
        this.pingInterval = pingInterval;
    }

    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    public int getKeepAliveDuration() {
        return keepAliveDuration;
    }

    public void setKeepAliveDuration(int keepAliveDuration) {
        this.keepAliveDuration = keepAliveDuration;
    }

    public String getCacheType() {
        return cacheType;
    }

    public void setCacheType(String cacheType) {
        this.cacheType = cacheType;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }
}
