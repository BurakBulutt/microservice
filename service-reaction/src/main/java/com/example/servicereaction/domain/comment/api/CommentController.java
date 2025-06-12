package com.example.servicereaction.domain.comment.api;

import com.example.servicereaction.domain.comment.mapper.CommentApiMapper;
import com.example.servicereaction.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<CommentResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(CommentApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("filter")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<CommentResponse>> filter(Pageable pageable,@RequestParam(required = false) String query) {
        return ResponseEntity.ok(CommentApiMapper.toPageResponse(service.filter(pageable,query)));
    }

    @GetMapping("target-comments")
    public ResponseEntity<Page<CommentResponse>> getByTarget(Pageable pageable,@RequestParam String target) {
        return ResponseEntity.ok(CommentApiMapper.toPageResponse(service.getByTarget(pageable,target)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CommentResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(CommentApiMapper.toResponse(service.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> save(@RequestBody @Valid AddCommentRequest request) {
        service.save(CommentApiMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> update(@PathVariable String id,@RequestBody @Valid UpdateCommentRequest request) {
        service.update(id,CommentApiMapper.toDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
