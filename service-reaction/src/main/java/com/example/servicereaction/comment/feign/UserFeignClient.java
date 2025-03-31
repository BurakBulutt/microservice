package com.example.servicereaction.comment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "service-users", path = "users",fallbackFactory = UserFallbackFactory.class)
public interface UserFeignClient {
    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getById(@PathVariable String id, @RequestHeader("X-Correlation-Id") String correlationId);
}
