package com.example.servicemedia.feign.fallback;

import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.feign.like.LikeFeignClient;
import com.example.servicemedia.feign.like.LikeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikeFallbackFactory implements FallbackFactory<LikeFeignClient> {

    @Override
    public LikeFeignClient create(Throwable cause) {
        return new LikeFeignClient() {
            @Override
            public ResponseEntity<LikeCountResponse> getLikeCount(String targetId) {
                log.error("Failed to getting likes: {},  Cause: {}",targetId,cause.getMessage());
                return ResponseEntity.internalServerError().build();
            }

            @Override
            public ResponseEntity<String> getTopTarget(LikeType likeType) {
                log.error("Failed to getting top target: {},  Cause: {}",likeType,cause.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        };
    }
}
