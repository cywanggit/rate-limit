package com.example.demo.collection;

import cn.hutool.core.date.StopWatch;
import com.example.demo.base.PhoneType;
import com.example.demo.dao.NativePhoneDao;
import com.example.demo.entity.PhoneEntity;
import com.example.demo.shard.NativePhoneShard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.demo.util.MobileUtil;

/**
 * @author cw
 * @copyright (C),  2020-2020, 创蓝253
 * @description
 * @FileName MobileContainer
 * @create 2020-04-11 16:15
 */
@Slf4j
@Component
public class MobileJudgeManager implements InitializingBean,MobileJudgeService {

    private Integer pageSize = 300000;


    @Autowired
    NativePhoneDao nativePhoneDao;

    @Qualifier("native.phone.shard")
    @Autowired
    NativePhoneShard nativePhoneShard;
    /**
     * 系统级别容器 key 容器编号  value 手机号能力map key 手机号 value 能力 boolean[0] = native  boolean[1] = white boolean[2] = black
     *
     */
    private Map<Integer,BitSet> sysContainer = new ConcurrentHashMap<>();


    @Override
    public void afterPropertiesSet(){
        init();
    }

    /**
     * 初始化内存格式
     */
    private void init() {
        String colection = nativePhoneShard.generateColectionName();
        long currentTimeMillis = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(30);
        //系统初始化
        int totalCount = 0;
        for (int i = 0; i < 10; i++) {
            String collectionName  = colection + "_" + i;
            Query countQuery = new Query();
            long count = nativePhoneDao.count(countQuery,collectionName);
            int listSize = (int) count  / pageSize +  1;
            totalCount += listSize;
        }
        CountDownLatch countDownLatch = new CountDownLatch(totalCount);
        int totalCount1 = 0;
        for (int i = 0; i < 10; i++) {
            String collectionName  = colection + "_" + i;
            Query countQuery = new Query();
            long count = nativePhoneDao.count(countQuery,collectionName);
            int listSize = (int) count  / pageSize +  1;
            totalCount1 += listSize;
            System.out.println(totalCount1);
            for(int j = 0; j < listSize; j++){
                int start =  j * pageSize;
                executor.execute(new QueryJob(start,pageSize,countDownLatch,collectionName,nativePhoneDao));
            }
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("初始化native手机号完成,花费" + (System.currentTimeMillis() - currentTimeMillis));
        executor.shutdown();
        int total = 0;
        for (Map.Entry<Integer, BitSet> entry : sysContainer.entrySet()) {
            Integer k = entry.getKey();
            BitSet v = entry.getValue();
            int cardinality = v.cardinality();
            total += cardinality;
//            log.info("k:{},v数量：{}", k,cardinality);
        }

        log.info("总数是：{}",total);
    }







    private boolean checkPhoneFormat(String phone){
        String regex = "^1\\d{2}\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }


    public static void main(String[] args) {

    }


    @Override
    public boolean addSysAbility(String phone, Integer businessNo) {
        Integer local = MobileUtil.getSysLocation(phone);
        BitSet bitSet = sysContainer.get(local);
        if (bitSet == null) {
            bitSet = new BitSet(9999999);
        }
        if (PhoneType.NATIVE_TYPE.equals(businessNo)) {
            bitSet.set(MobileUtil.getPhoneValue(phone),true);
        }
        sysContainer.put(local, bitSet);
        return true;
    }

    @Override
    public boolean addSysAbilityForDB(String mobile, Integer localtion, Integer businessNo) {
        BitSet bitSet = sysContainer.get(localtion);
        if (bitSet == null) {
            bitSet = new BitSet(9999999);
        }
        if (PhoneType.NATIVE_TYPE.equals(businessNo)) {
            bitSet.set(Integer.valueOf(mobile),true);
        }
        sysContainer.put(localtion, bitSet);
        return true;
    }

    @Override
    public boolean getSysAbility(String phone,Integer businessNo){
        Integer local = MobileUtil.getSysLocation(phone);
        BitSet bitSet = sysContainer.get(local);
        if (bitSet == null) {
            bitSet = new BitSet();
        }
        return bitSet.get(MobileUtil.getPhoneValue(phone));
    }



    class QueryJob implements Runnable {

        private long start;

        private int limit;

        CountDownLatch countDownLatch;

        NativePhoneDao nativePhoneDao;

        String collection;

        public QueryJob(long start, int limit, CountDownLatch countDownLatch,String collection,NativePhoneDao nativePhoneDao) {
            this.start = start;
            this.limit = limit;
            this.countDownLatch = countDownLatch;
            this.nativePhoneDao = nativePhoneDao;
            this.collection = collection;
        }

        @Override
        public void run() {
            StopWatch stopWatch = new StopWatch(collection+":"+start/pageSize);
            stopWatch.start("query");
            List<PhoneEntity> phoneEntities = nativePhoneDao.queryList(start,limit,collection);
            stopWatch.stop();
            stopWatch.start("put");
            if (phoneEntities != null && phoneEntities.size() > 0) {
                //根据手机号算出对应的所放的Map容器
                for (PhoneEntity phoneEntity : phoneEntities) {
                    addSysAbilityForDB(phoneEntity.getMobile(),Integer.valueOf(phoneEntity.getNumber()),PhoneType.NATIVE_TYPE);
                }
            }
            stopWatch.stop();
            log.info(""+stopWatch.prettyPrint());
            countDownLatch.countDown();
        }
    }


}
