package com.example.servicemedia.feign.like;

import com.example.servicemedia.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "service-reaction",contextId = "likeClient",path = "likes",fallbackFactory = LikeFallbackFactory.class,configuration = FeignConfig.class)
public interface LikeFeignClient {

    @GetMapping("like-count/{targetId}")
    ResponseEntity<LikeCountResponse> getLikeCount(@PathVariable String targetId);

    @GetMapping("top-like")
    ResponseEntity<String> getTopTarget(@RequestParam LikeType likeType);
}
