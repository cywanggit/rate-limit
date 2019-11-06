package com.chuanglan.entity;


/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description 基于内存进行限流（适用于单机）
 * @FileName Rate
 * @auther cw
 * @create 2019-11-01 9:45
 */
public class MemoryRate implements Rate {

    private Object lockTime = new Object();

    private Object lockRequestTime = new Object();

    private long limitTimeMillis;

    private long time;

    private volatile long currentTime = 0; //当前通过次数

    private volatile long requestTime = 0; // 请求次数 可以做 接口的访问量的计数

    private volatile long startLimitTimeMillis; //本次限制的开始时间

    public MemoryRate() {

    }

    public MemoryRate(long limitTimeMillis, long time) {
        this.limitTimeMillis = limitTimeMillis;
        this.time = time;
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

    public long getStartLimitTimeMillis() {
        return startLimitTimeMillis;
    }

    public void setStartLimitTimeMillis(long startLimitTimeMillis) {
        if (this.startLimitTimeMillis + this.limitTimeMillis > startLimitTimeMillis){
            return ;
        }
        this.startLimitTimeMillis = startLimitTimeMillis;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public void addTime(){
        synchronized (lockTime){
            currentTime ++;
        }
    }

    public long getRequestTime() {
        return this.requestTime;
    }

    @Override
    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public boolean getLock() {
        return true;
    }

    @Override
    public void releaseLock() {
    }

    public void addRequestTime() {
        synchronized (lockRequestTime){
            requestTime ++;
        }
    }

}

