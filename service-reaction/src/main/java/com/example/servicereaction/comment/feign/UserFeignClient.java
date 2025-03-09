package com.example.servicereaction.comment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-users", path = "/api/v1/users")
public interface UserFeignClient {
    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getById(@PathVariable String id);
}
