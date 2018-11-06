package com.lanyage.eureka.commonapi.service;


import com.lanyage.eureka.commonapi.domain.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface UserApiService {
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    List<User> findAll();

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    User findOne(@PathVariable("id") Integer id);
}
