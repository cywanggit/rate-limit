package com.chuanglan.example;

import com.chuanglan.config.DefaultRatelimiter;
import com.chuanglan.config.RateLimiter;
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
    }

    public void test() throws InterruptedException {
        Rate rate = new MemoryRate(1000,500);
//        Rate rate = new RedisRate("localhost",6379,"","");
        RateLimiter rateLimiter = new DefaultRatelimiter(rate);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 600; i++) {
            executor.execute(new RateTest("" + i,rate,rateLimiter));
        }
        System.out.println(System.currentTimeMillis());
        Thread.sleep(1000);
        for (int i = 0; i < 1000; i++) {
            executor.execute(new RateTest("" + (i+1000),rate,rateLimiter));
        }
        executor.shutdown();
    }
    class RateTest implements Runnable {
        private Rate rate;
        private RateLimiter rateLimiter;
        private String name;

        public RateTest(String name,Rate rate, RateLimiter rateLimiter) {
            this.rate = rate;
            this.rateLimiter = rateLimiter;
            this.name = name;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
//            boolean limit = rateLimiter.limit();
//            System.out.println("花费：" + (System.currentTimeMillis() - start)+ "当前是 " + name + "； 当前次数:" + rate.getCurrentTime() + "；请求次数 :" + rate.getRequestTime() +";结果是:" + limit);

            while(!rate.getLock()){
            }
            boolean limit = rateLimiter.limit();
            rate.releaseLock();
            System.out.println("花费：" + (System.currentTimeMillis() - start)+ "当前是 " + name + "； 当前次数:" + rate.getCurrentTime() + "；请求次数 :" + rate.getRequestTime() +";结果是:" + limit);
        }
    }
}
