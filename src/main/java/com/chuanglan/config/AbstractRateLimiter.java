package com.chuanglan.config;

import com.chuanglan.entity.MemoryRate;
import com.chuanglan.entity.Rate;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description 抽象类
 * @FileName AbstractRateLimiter
 * @auther cw
 * @create 2019-11-01 9:44
 */
public abstract class AbstractRateLimiter implements RateLimiter{

    protected Rate rate;

    protected boolean isNeedRequestTime = false; //是否需要统计请求次数

    protected abstract void setRate(Rate rate) ;

    public boolean limit() {
       return limit(isNeedRequestTime);
    }

    @Override
    public void isNeedRequestTime(boolean isNeedRequestTime) {
        this.isNeedRequestTime = isNeedRequestTime;
    }

    public boolean limit(boolean isNeedRequestTime) {
        if (rate == null){
            throw new IllegalArgumentException("参数异常");
        }
        if (isNeedRequestTime){
            rate.addRequestTime();
        }
        long current = System.currentTimeMillis();
        if (current < rate.getStartLimitTimeMillis()){
            throw new IllegalArgumentException("开始时间异常");
        }
        if (current > (rate.getStartLimitTimeMillis() + rate.getLimitTimeMillis()) ){
            rate.setCurrentTime(1);
            rate.setStartLimitTimeMillis(current);
            if (isNeedRequestTime){
                rate.setRequestTime(1);
            }
            return true;
        }
        if (rate.getCurrentTime() >= rate.getTime()){
            return false;
        }
        rate.addTime();
        return true;
    }


}
