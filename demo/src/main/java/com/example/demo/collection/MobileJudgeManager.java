package com.example.demo.collection;

import cn.hutool.core.date.StopWatch;
import com.example.demo.base.PhoneType;
import com.example.demo.dao.NativePhoneDao;
import com.example.demo.entity.PhoneEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final static String ABILITY_SPLIT = "-";

    private final static String ACCOUNT_SPLIT = ",";



    @Autowired
    NativePhoneDao nativePhoneDao;


    /**
     * 系统级别容器 key 容器编号  value 手机号能力map key 手机号 value 能力 boolean[0] = native  boolean[1] = white boolean[2] = black
     *
     */
//    private Map<Integer, Set<String>> sysContainer = new HashMap<>();

    private Map<Integer,BitSet> sysContainer = new ConcurrentHashMap<>();

    /**
     * 用户级别key 手机号  v 账号-能力编号  多个用逗号隔开
     */




    @Override
    public void afterPropertiesSet(){


        long currentTimeMillis = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(30);
        //系统初始化
        Query countQuery = new Query();
        long count = nativePhoneDao.count(countQuery);
        int listSize = (int) count  / pageSize +  1;
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
    /**true为校验成功 flase为校验失败*/
    private boolean checkPhoneFormat(String phone){
        String regex = "^1\\d{2}\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }


    public static void main(String[] args) {

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

    @Override
    public boolean addSysAbility(String phone, Integer businessNo) {
        Integer local = getSysLocation(phone);
        BitSet bitSet = sysContainer.get(local);
        if (bitSet == null) {
            bitSet = new BitSet(9999999);
        }
        if (PhoneType.NATIVE_TYPE.equals(businessNo)) {
            bitSet.set(getPhoneValue(phone),true);
        }
        sysContainer.put(local, bitSet);
        return true;
    }







    @Override
    public boolean getSysAbility(String phone, Integer businessNo){
        Integer local = getSysLocation(phone);
        BitSet bitSet = sysContainer.get(local);
        if (bitSet == null) {
            bitSet = new BitSet();
        }
        return bitSet.get(getPhoneValue(phone));
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
            List<PhoneEntity> phoneEntities = nativePhoneDao.queryList(start,limit);
            stopWatch.stop();
            stopWatch.start("put");
            if (phoneEntities != null && phoneEntities.size() > 0) {
                //根据手机号算出对应的所放的Map容器
                for (PhoneEntity phoneEntity : phoneEntities) {
                    if (!StringUtils.isEmpty(phoneEntity.getMobile()) && checkPhoneFormat(phoneEntity.getMobile())){
                        addSysAbility(phoneEntity.getMobile(),PhoneType.NATIVE_TYPE);
                    }else{
                        log.warn("手机号格式不正确:{}",phoneEntity.getMobile());
                    }
                }
            }
            stopWatch.stop();
            log.info(""+stopWatch.prettyPrint());
            countDownLatch.countDown();
        }
    }


}
