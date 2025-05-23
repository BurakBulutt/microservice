package com.example.servicereaction.domain.like.api;

import com.example.servicereaction.domain.like.enums.LikeType;
import com.example.servicereaction.domain.like.mapper.LikeApiMapper;
import com.example.servicereaction.domain.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService service;

    @GetMapping("like-count/{targetId}")
    public ResponseEntity<LikeCountResponse> getLikeCount(@PathVariable String targetId) {
        return ResponseEntity.ok(LikeApiMapper.toResponse(service.findLikeCount(targetId)));
    }

    @GetMapping("top-like")
    public ResponseEntity<String> getTopTarget(@RequestParam LikeType likeType) {
        return ResponseEntity.ok(service.getTopContentLikeTarget(likeType));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody LikeRequest request) {
        service.save(LikeApiMapper.toLikeDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
