package com.example.servicereaction.feign.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContentFallbackFactory implements FallbackFactory<ContentFeignClient> {

    @Override
    public ContentFeignClient create(Throwable cause) {
        return new ContentFeignClient() {
            @Override
            public ResponseEntity<ContentResponse> getById(String id) {
                log.error("Failed to getting content: {}, Cause: {}",id,cause.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        };
    }
}
