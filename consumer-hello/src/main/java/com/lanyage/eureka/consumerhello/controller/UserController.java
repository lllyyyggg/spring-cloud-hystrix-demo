package com.lanyage.eureka.consumerhello.controller;

import com.lanyage.eureka.commonapi.domain.User;
import com.lanyage.eureka.consumerhello.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @HystrixCommand(fallbackMethod = "users")
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<com.lanyage.eureka.commonapi.domain.User> userList() {
        return userService.findAll();
    }

    public List<User> users() {
        List<User> users = Collections.emptyList();
        return users;
    }

    @HystrixCommand(fallbackMethod = "user")
    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public com.lanyage.eureka.commonapi.domain.User userDetails(@PathVariable("id") Integer id) {
        return userService.findOne(id);
    }

    public User user(Integer id) {
        return new User(id, "NOT FOUND", "NOT FOUND");
    }
}
