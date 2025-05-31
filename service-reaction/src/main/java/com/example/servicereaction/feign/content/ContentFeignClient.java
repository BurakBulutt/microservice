package com.example.servicereaction.feign.content;

import com.example.servicereaction.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-media",contextId = "content-client", path = "contents",fallbackFactory = ContentFallbackFactory.class,configuration = FeignConfig.class)
public interface ContentFeignClient {
    @GetMapping("/{id}")
    ResponseEntity<ContentResponse> getById(@PathVariable String id);
}
