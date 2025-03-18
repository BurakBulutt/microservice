package com.example.servicemedia.content.api;

import com.example.servicemedia.content.mapper.ContentApiMapper;
import com.example.servicemedia.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Page<ContentResponse>> getNewContents() {
        return ResponseEntity.ok(ContentApiMapper.toPageResponse(service.getNewContents()));
    }

    @GetMapping("search")
    public ResponseEntity<List<ContentSearchResponse>> getNewContents(@RequestParam(required = true) String query) {
        return ResponseEntity.ok(ContentApiMapper.toSearchResponses(service.searchFilter(query)));
    }

    @GetMapping("filter")
    public ResponseEntity<Page<ContentResponse>> filter(@RequestParam(required = false) String category,@RequestParam(required = false) String sortBy, Pageable pageable) {
        return ResponseEntity.ok(ContentApiMapper.toPageResponse(service.filter(category,sortBy,pageable)));
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
    public ResponseEntity<Void> save(@RequestBody ContentRequest request) {
        service.save(ContentApiMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable String id,@RequestBody ContentRequest request) {
        service.update(id,ContentApiMapper.toDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
