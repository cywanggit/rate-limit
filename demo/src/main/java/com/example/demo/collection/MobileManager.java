package com.example.demo.collection;

import cn.hutool.core.collection.CollectionUtil;
import com.example.demo.dao.NativePhoneDao;
import com.example.demo.entity.PhoneEntity;
import com.example.demo.shard.NativePhoneShard;
import com.example.demo.util.MobileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName MobileManager
 * @create 2020-04-20 14:27
 */
@Component
@Slf4j
public class MobileManager implements InitializingBean {
    private String filePath = "D:\\active\\active.txt";
    private int pageSize = 1000000;
    @Autowired
    MobileJudgeManager mobileJudgeManager;

    @Autowired
    NativePhoneDao nativePhoneDao;

    @Qualifier("native.phone.shard")
    @Autowired
    NativePhoneShard nativePhoneShard;

    public void init(){
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            long count = bufferedReader.lines().count();
            long l = count / pageSize  + 1;
            CountDownLatch countDownLatch = new CountDownLatch((int) l);
            for (int i = 0; i < l; i++) {
                long start = i* pageSize;
                long end = start + pageSize;
                executorService.execute(new FileToDbJob(start, end,countDownLatch));
            }
            countDownLatch.await();
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    private boolean checkPhoneFormat(String phone){
        String regex = "^1\\d{2}\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public void fileToDB(long start,long end) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
            String s = null;
            long count =0;
            long readNum = 0;
            long insertNum = 0;
            Map<String, List<PhoneEntity>> map = new HashMap<>();
            while (null != (s= bufferedReader.readLine())){

                if ( ++count< start+1){
                    continue;
                }
                readNum++;
                if (checkPhoneFormat(s)){
                    insertNum++;
                    PhoneEntity phone = new PhoneEntity();
                    phone.setMobile(MobileUtil.getPhoneValue(s) + "");
                    phone.setNumber(MobileUtil.getSysLocationForString(s));
                    List<PhoneEntity> strings = map.get(phone.getNumber());
                    if (CollectionUtil.isEmpty(strings)){
                        strings = new ArrayList<PhoneEntity>();
                    }
                    strings.add(phone);
                    map.put(phone.getNumber(), strings);
                }else {
                    log.info("手机号{}格式不正确",s);
                }
                if (count >= end){
                    break;
                }
            }
            String colectionNamePrefix = nativePhoneShard.generateColectionName();

            map.forEach((number,mobiles) -> {
                String collectionName = colectionNamePrefix +"_"+ number.substring(2);
                nativePhoneDao.insertBatch(collectionName, mobiles);
            });
            log.info("从{}到{}插入db完成,读取数量{},插入数量{}",start,end,readNum,insertNum);
        } catch (IOException e) {

        }finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        init();
    }


    class FileToDbJob implements Runnable {
        private long start;

        private long end;

        CountDownLatch countDownLatch;

        public FileToDbJob(long start, long end,CountDownLatch countDownLatch) {
            this.start = start;
            this.end = end;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            fileToDB(start, end);
            countDownLatch.countDown();
        }
    }

}
