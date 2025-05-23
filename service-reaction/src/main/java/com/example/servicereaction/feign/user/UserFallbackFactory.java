package com.example.servicereaction.feign.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public ResponseEntity<UserResponse> getById(String id) {
                log.error("Failed to getting user: {}, Cause: {}",id,cause.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        };
    }
}
