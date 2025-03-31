package com.example.servicereaction.comment.api;

import com.example.servicereaction.comment.mapper.CommentApiMapper;
import com.example.servicereaction.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(CommentApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(CommentApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("/comment/{targetId}")
    public ResponseEntity<Page<CommentResponse>> getByTargetId(@PathVariable String targetId,Pageable pageable) {
        return ResponseEntity.ok(CommentApiMapper.toPageResponse(service.getByTargetId(targetId,pageable)));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody AddCommentRequest request) {
        service.save(CommentApiMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable String id,@RequestBody UpdateCommentRequest request) {
        service.update(id,CommentApiMapper.toDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
