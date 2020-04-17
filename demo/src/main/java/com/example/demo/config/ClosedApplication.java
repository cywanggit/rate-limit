package com.example.demo.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @author cw
 * @Copyright (C),  2020-2020, 创蓝253
 * @Description
 * @FileName ClosedApplication
 * @create 2020-04-15 15:11
 */
@Component
public class ClosedApplication implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ContextClosedEvent){
            System.out.println(event.getClass().getSimpleName()+" 事件已发生！");
        }else if(event instanceof ContextStoppedEvent){
            System.out.println(event.getClass().getSimpleName()+" 事件已发生！");
        }

    }
}
