package com.example.servicereaction.feign.user;

import com.example.servicereaction.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-users", path = "users",fallbackFactory = UserFallbackFactory.class,configuration = FeignConfig.class)
public interface UserFeignClient {
    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getById(@PathVariable String id);
}
