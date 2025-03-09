package com.example.servicemedia.category.api;

import com.example.servicemedia.category.mapper.CategoryApiMapper;
import com.example.servicemedia.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(CategoryApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(CategoryApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("slug/{slug}")
    public ResponseEntity<CategoryResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(CategoryApiMapper.toResponse(service.getBySlug(slug)));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> save(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(CategoryApiMapper.toResponse(service.save(CategoryApiMapper.toDto(request))));
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable String id,@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(CategoryApiMapper.toResponse(service.update(id,CategoryApiMapper.toDto(request))));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
