package com.example.demo;

import com.example.demo.collection.MobileJudgeService;
import com.example.demo.dao.NativePhoneDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class})
public class DemoApplicationTests {
    private Integer pageSize = 100000;

    @Autowired
    MobileJudgeService mobileJudgeService;


    @Test
    public void contextLoads() {
        mobileJudgeService.getSysAbility("18656710171",1);

    }


}
