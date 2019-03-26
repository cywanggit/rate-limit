package com.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/hello")
public class HelloController {

    @Value("${server.port}")
    private String port;

    @RequestMapping("/say")
    public String sayHello(){
        return "hello"+port;
    }
}
