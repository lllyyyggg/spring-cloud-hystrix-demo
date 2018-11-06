package com.lanyage.eureka.consumerhello.service;

import com.lanyage.eureka.commonapi.service.UserApiService;
import com.lanyage.eureka.consumerhello.fallback.UserServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "SERVICE-HELLO", fallback = UserServiceFallBack.class)
public interface UserService extends UserApiService {
}
