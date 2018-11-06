package com.lanyage.eureka.consumerhello.fallback;

import com.lanyage.eureka.commonapi.domain.User;
import com.lanyage.eureka.consumerhello.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserServiceFallBack implements UserService {
    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }

    @Override
    public User findOne(Integer id) {
        return new User(id, "NOT FOUND", "NOT FOUND");
    }
}
