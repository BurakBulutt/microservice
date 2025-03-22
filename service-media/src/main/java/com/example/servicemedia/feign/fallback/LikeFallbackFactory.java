package com.example.servicemedia.feign.fallback;

import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.feign.like.LikeFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikeFallbackFactory implements FallbackFactory<LikeFeignClient> {

    @Override
    public LikeFeignClient create(Throwable cause) {
        return new LikeFeignClient() {
            @Override
            public ResponseEntity<LikeCountResponse> getLikeCount(String correlationId, String targetId, String userId) {
                log.warn("{}: Like Servise Erişilemedi: {}",correlationId,cause.getMessage());
                return ResponseEntity.noContent().build();
            }
        };
    }
}
