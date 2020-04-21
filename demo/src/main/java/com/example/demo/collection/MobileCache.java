package com.example.demo.collection;

import cn.hutool.core.date.StopWatch;

import com.example.demo.base.PhoneType;
import com.example.demo.dao.NativePhoneDao;
import com.example.demo.entity.PhoneEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName MobileCache
 * @create 2020-04-15 19:55
 */

@Slf4j
@Component
public class MobileCache {

    private Integer pageSize = 1000000;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Autowired
    NativePhoneDao nativePhoneDao;

    public void mobileToRedis(){
        long currentTimeMillis = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        //系统初始化
//        Query countQuery = new Query();
//        long count = nativePhoneDao.count(countQuery);
        int listSize = 10;
        CountDownLatch countDownLatch = new CountDownLatch(listSize);

        for (int i = 0; i < listSize; i++) {
            int i1 = i * pageSize;
            executor.execute(new QueryJob(i1,pageSize,countDownLatch,nativePhoneDao));
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("初始化native手机号完成,花费" + (System.currentTimeMillis() - currentTimeMillis));



    }
    public void addNativeMobileToRedis(String phone){
        addSysAbility(phone);
    }


    public void deleteNativeMobileToRedis(String phone){
        deleteSysAbility(phone);
    }

    private boolean deleteSysAbility(String phone){
        Integer local = getSysLocation(phone);
        stringRedisTemplate.opsForSet().remove(String.format("rcs:admin:api:native:%s",local),getPhoneValue(phone)+"");
        return true;
    }

    /**
     * 根据手机号去 求出 该手机号放在系统容器中的第几个
     * TODO 这种方式 可能造成某一map 数量很多。某一map数量很少
     * @param phone
     * @return
     */
    private Integer getSysLocation(String phone){
        String last3 = phone.substring(8);
        return Integer.valueOf(last3);
    }

    /**
     * 获取存储的value
     * @param phone
     * @return
     */
    private int getPhoneValue(String phone) {
        String front8 = phone.substring(0,8);
        return Integer.parseInt(front8.substring(1));
    }


    /**
     * 添加系统级别手机号缓存
     * @param phone
     * @return
     */
    public boolean addSysAbility(String phone) {
        Integer local = getSysLocation(phone);
        stringRedisTemplate.opsForSet().add(String.format("rcs:admin:api:native:%s",local),getPhoneValue(phone)+"");
        return true;
    }


    class QueryJob implements Runnable {

        private long start;

        private int limit;

        CountDownLatch countDownLatch;

        NativePhoneDao nativePhoneDao;

        public QueryJob(long start, int limit, CountDownLatch countDownLatch,NativePhoneDao nativePhoneDao) {
            this.start = start;
            this.limit = limit;
            this.countDownLatch = countDownLatch;
            this.nativePhoneDao = nativePhoneDao;
        }

        @Override
        public void run() {
            StopWatch stopWatch = new StopWatch(start/pageSize+"");
            stopWatch.start("query");
            //分页查出手机号做处理
            List<PhoneEntity> phoneEntities = nativePhoneDao.queryList(start,limit,"");
            stopWatch.stop();
            stopWatch.start("put");
            if (phoneEntities != null && phoneEntities.size() > 0) {
                //根据手机号算出对应的所放的Map容器
                for (PhoneEntity phoneEntity : phoneEntities) {
                    if (!StringUtils.isEmpty(phoneEntity.getMobile()) && phoneEntity.getMobile().length() > 10){
                        addSysAbility(phoneEntity.getMobile());
                    }
                }
            }
            stopWatch.stop();
            System.out.println(stopWatch.prettyPrint());
            countDownLatch.countDown();
        }
    }



}
