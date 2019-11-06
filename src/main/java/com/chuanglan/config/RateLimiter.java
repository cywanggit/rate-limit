package com.chuanglan.config;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description 限制
 * @FileName RateLimiter
 * @auther cw
 * @create 2019-11-01 9:42
 */
public interface RateLimiter {

    boolean limit();

    void isNeedRequestTime(boolean isNeedRequestTime);
}
