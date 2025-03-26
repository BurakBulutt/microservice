package com.example.servicereaction.comment.feign;

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
            public ResponseEntity<UserResponse> getById(String correlationId, String id) {
                log.warn("{}: Connection Fail to User Service: {}",correlationId,cause.getMessage());
                return ResponseEntity.noContent().build();
            }
        };
    }
}
