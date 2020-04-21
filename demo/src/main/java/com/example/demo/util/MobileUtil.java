package com.example.demo.util;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName MobileUtil
 * @create 2020-04-20 15:49
 */
public class MobileUtil {


    /**
     * 根据手机号去 求出 该手机号放在系统容器中的第几个
     * TODO 这种方式 可能造成某一map 数量很多。某一map数量很少
     * @param phone
     * @return
     */
    public static Integer getSysLocation(String phone){
        String last3 = phone.substring(8);
        return Integer.valueOf(last3);
    }


    public static String getSysLocationForString(String phone){
        String last3 = phone.substring(8);
        return last3;
    }

    /**
     * 获取存储的value
     * @param phone
     * @return
     */
    public static int getPhoneValue(String phone) {
        String front8 = phone.substring(0,8);
        return Integer.parseInt(front8.substring(1));
    }

}
