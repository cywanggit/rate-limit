package com.chuanglan.config;

import com.chuanglan.entity.Rate;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description
 * @FileName MemoryRatelimiter
 * @auther cw
 * @create 2019-11-01 10:46
 */
public class DefaultRatelimiter extends AbstractRateLimiter{

    public DefaultRatelimiter(Rate rate) {
        this.rate = rate;
        this.rate.setStartLimitTimeMillis(System.currentTimeMillis());
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }
}
