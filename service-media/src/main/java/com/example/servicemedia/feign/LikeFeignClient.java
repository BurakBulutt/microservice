package com.example.servicemedia.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "service-reaction",contextId = "likeClient",path = "/api/v1/likes")
public interface LikeFeignClient {
    @GetMapping("like-count/{targetId}")
    ResponseEntity<LikeCountResponse> getLikeCount(@PathVariable String targetId, @RequestParam(required = false) String userId);
}
