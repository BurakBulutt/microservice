package com.example.servicereaction.feign.media;

import com.example.servicereaction.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-media", contextId = "media-client", path = "medias",fallbackFactory = MediaFallbackFactory.class,configuration = FeignConfig.class)
public interface MediaFeignClient {
    @GetMapping("/{id}")
    ResponseEntity<MediaResponse> getById(@PathVariable String id);
}
