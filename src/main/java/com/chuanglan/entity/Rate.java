package com.chuanglan.entity;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description 限制统一接口
 * @FileName Rate
 * @auther cw
 * @create 2019-11-01 10:38
 */
public interface Rate {

    long getLimitTimeMillis();

    long getTime();

    long getStartLimitTimeMillis();

    void setStartLimitTimeMillis(long startLimitTimeMillis);

    long getCurrentTime();

    void setCurrentTime(long currentTime);

    void addTime();

    void addRequestTime();

    long getRequestTime();

    void setRequestTime(long requestTime);

    boolean getLock();

    void releaseLock();
}
