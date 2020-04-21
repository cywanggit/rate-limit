package com.example.demo;

import com.example.demo.collection.MobileJudgeManager;
import com.example.demo.collection.MobileManager;
import com.example.demo.dao.NativePhoneDao;
import com.example.demo.entity.PhoneEntity;
import com.example.demo.shard.NativePhoneShard;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class})
public class DemoApplicationTests {


    @Autowired
    MobileJudgeManager mobileJudgeManager;

    @Autowired
    NativePhoneDao nativePhoneDao;

    @Qualifier("native.phone.shard")
    @Autowired
    NativePhoneShard nativePhoneShard;

    private int size = 300000;

    @Autowired
    MobileManager mobileManager;


    @Test
    public void contextLoads() throws IOException {
        mobileManager.init();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\ufffd\\uff";
        String Q=new String(s.getBytes("GBK"));
        System.out.println(Q);
    }

}
