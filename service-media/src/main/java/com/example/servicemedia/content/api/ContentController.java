package com.example.servicemedia.content.api;

import com.example.servicemedia.content.mapper.ContentApiMapper;
import com.example.servicemedia.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/contents")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService service;

    @GetMapping
    public ResponseEntity<Page<ContentResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ContentApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("new-content")
    public ResponseEntity<Page<ContentResponse>> getNewContents(Pageable pageable) {
        return ResponseEntity.ok(ContentApiMapper.toPageResponse(service.getNewContents(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(ContentApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ContentResponse> getBySlug(@PathVariable String slug,@RequestParam(required = false) String userId) {
        return ResponseEntity.ok(ContentApiMapper.toResponse(service.getBySlug(slug,userId)));
    }

    @PostMapping
    public ResponseEntity<ContentResponse> save(@RequestBody ContentRequest request) {
        return ResponseEntity.ok(ContentApiMapper.toResponse(service.save(ContentApiMapper.toDto(request))));
    }

    @PutMapping("{id}")
    public ResponseEntity<ContentResponse> update(@PathVariable String id,@RequestBody ContentRequest request) {
        return ResponseEntity.ok(ContentApiMapper.toResponse(service.update(id,ContentApiMapper.toDto(request))));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
