package com.example.servicereaction.like.api;

import com.example.servicereaction.like.mapper.LikeApiMapper;
import com.example.servicereaction.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService service;

    @GetMapping("like-count/{targetId}")
    public ResponseEntity<LikeCountResponse> getLikeCount(@PathVariable String targetId, @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(LikeApiMapper.toResponse(service.findLikeCount(targetId,userId)));
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
