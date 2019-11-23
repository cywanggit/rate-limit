package com.chuanglan.example;

import com.chuanglan.config.DefaultRatelimiter;
import com.chuanglan.config.RateLimiter;
import com.chuanglan.config.RedisLuaRateLimiter;
import com.chuanglan.entity.MemoryRate;
import com.chuanglan.entity.Rate;
import com.chuanglan.entity.RedisRate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @copyright (C),  2019-2019, 创蓝253
 * @Description demo
 * @FileName Demo
 * @auther cw
 * @create 2019-11-01 11:51
 */
public class Demo {
    public static void main(String[] args) throws InterruptedException {
        Demo demo = new Demo();
        demo.test();
//        demo.testNewRedisLimiter();
    }


    public void testNewRedisLimiter() throws InterruptedException {
        Rate rate = new RedisRate("localhost",6379,"default_limitTimeMillis","default_time");
        RateLimiter rateLimiter = new RedisLuaRateLimiter(rate);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 600; i++) {
            executor.execute(new RedisRateTest("" + i,rate,rateLimiter));
        }


        for (int i = 0; i < 1000; i++) {
            executor.execute(new RedisRateTest("" + (i+600),rate,rateLimiter));
        }
        executor.shutdown();
    }

    public void test() throws InterruptedException {
        Rate rate = new MemoryRate(1000,10);
        RateLimiter rateLimiter = new DefaultRatelimiter(rate);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 30; i++) {
            executor.execute(new MemoryRateTest("" + i,rate,rateLimiter));
        }
        for (int i = 0; i < 10; i++) {
            executor.execute(new MemoryRateTest("" + (i+30),rate,rateLimiter));
        }
        executor.shutdown();
    }
    class MemoryRateTest implements Runnable {
        private Rate rate;
        private RateLimiter rateLimiter;
        private String name;

        public MemoryRateTest(String name,Rate rate, RateLimiter rateLimiter) {
            this.rate = rate;
            this.rateLimiter = rateLimiter;
            this.name = name;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            boolean limit = rateLimiter.limit();
            System.out.println("当前时间:"+ rate.getStartLimitTimeMillis()+ "花费：" + (System.currentTimeMillis() - start)+ "当前是 " + name + "； 当前次数:" + rate.getCurrentTime() + ";结果是:" + limit);
        }
    }

    class RedisRateTest implements Runnable {
        private Rate rate;
        private RateLimiter rateLimiter;
        private String name;

        public RedisRateTest(String name,Rate rate, RateLimiter rateLimiter) {
            this.rate = rate;
            this.rateLimiter = rateLimiter;
            this.name = name;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            boolean limit = rateLimiter.limit();
            System.out.println("当前时间:"+ rate.getStartLimitTimeMillis()+ "花费：" + (System.currentTimeMillis() - start)+ "当前是 " + name + "； 当前次数:" + rate.getCurrentTime() +";结果是:" + limit);
        }
    }
}
