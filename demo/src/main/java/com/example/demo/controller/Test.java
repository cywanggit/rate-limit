package com.example.demo.controller;

import cn.hutool.core.date.StopWatch;
import com.example.demo.collection.MobileCache;
import com.example.demo.collection.MobileJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName Test
 * @create 2020-04-15 10:45
 */
@RestController
@RequestMapping
public class Test {

    @Autowired
    MobileJudgeService mobileJudgeService;


    @Autowired
    MobileCache mobileCache;
    @RequestMapping("/test")
    public void test(String phone,Integer businessNo, Integer time){
        StopWatch start = new StopWatch(phone);
        start.start("test");
        for (int i = 0; i < time; i++){
            mobileJudgeService.getSysAbility(phone, businessNo);
        }
        start.stop();
        System.out.println(start.prettyPrint());
    }

    @RequestMapping("/testToRedis")
    public void testToRedis(){
        mobileCache.mobileToRedis();
    }

    @RequestMapping("/addToRedis")
    public void addToRedis(String phone){
        mobileCache.addNativeMobileToRedis(phone);
    }

    @RequestMapping("/removeToRedis")
    public void removeToRedis(String phone){
        mobileCache.deleteNativeMobileToRedis(phone);
    }

}
