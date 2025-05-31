package com.example.servicereaction.feign.media;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MediaFallbackFactory implements FallbackFactory<MediaFeignClient> {

    @Override
    public MediaFeignClient create(Throwable cause) {
        return new MediaFeignClient() {
            @Override
            public ResponseEntity<MediaResponse> getById(String id) {
                log.error("Failed to getting media: {}, Cause: {}",id,cause.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        };
    }
}
