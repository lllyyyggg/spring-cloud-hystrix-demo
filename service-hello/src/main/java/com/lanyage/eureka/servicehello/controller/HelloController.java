package com.lanyage.eureka.servicehello.controller;

import com.lanyage.eureka.commonapi.service.UserApiService;
import com.lanyage.eureka.servicehello.domain.User;
import com.netflix.appinfo.EurekaInstanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController implements UserApiService {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private EurekaInstanceConfig eurekaInstanceConfig;

    @Value("${server.port}")
    private Integer serverPort;

    @GetMapping(value = "/hello")
    public String hello() {
        logger.info("/hello, InstanceId : {}, host:{}", eurekaInstanceConfig.getInstanceId(), eurekaInstanceConfig.getHostName(false));
        return "Hello, Spring Cloud! My port is " + String.valueOf(serverPort);
    }

    @GetMapping(value = "/users", produces = {"application/json;charset=UTF-8"})
    public List<com.lanyage.eureka.commonapi.domain.User> findAll() {
        return buildUsers();
    }

    @GetMapping(value = "/users/{id}", produces = {"application/json;charset=UTF-8"})
    public com.lanyage.eureka.commonapi.domain.User findOne(@PathVariable("id") Integer id) {
        return buildUsers().stream().filter(user -> user.getId() == id).findFirst().get();
    }

    private List<com.lanyage.eureka.commonapi.domain.User> buildUsers() {
        List<com.lanyage.eureka.commonapi.domain.User> userList = new ArrayList<>();
        userList.add(new com.lanyage.eureka.commonapi.domain.User(1, "lanyage", "lanyage"));
        userList.add(new com.lanyage.eureka.commonapi.domain.User(2, "shufang", "shufang"));
        userList.add(new com.lanyage.eureka.commonapi.domain.User(3, "daimengxiao", "daimengxiao"));
        return userList;
    }

}
