package com.example.demo.collection;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName MobileJudgeService
 * @create 2020-04-13 13:36
 */
public interface MobileJudgeService {


    /**
     * 添加系统级别手机号缓存
     * @param phone
     * @param businessNo
     * @return
     */
    boolean addSysAbility(String phone, Integer businessNo);


    /**
     * 拆分后的手机号 添加能力
     * @param mobile
     * @param localtion
     * @param businessNo
     * @return
     */
    public boolean addSysAbilityForDB(String mobile, Integer localtion, Integer businessNo);

    /**
     * 获取系统级别
     * @param phone 手机号
     * @param businessNo   业务No 1、native 2、white 3、black
     * @return
     */
    boolean getSysAbility(String phone,Integer businessNo);


}
