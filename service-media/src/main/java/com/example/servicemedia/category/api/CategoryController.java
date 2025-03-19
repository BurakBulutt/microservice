package com.example.servicemedia.category.api;

import com.example.servicemedia.category.mapper.CategoryApiMapper;
import com.example.servicemedia.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categories")
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
    public ResponseEntity<Void> save(@RequestBody CategoryRequest request) {
        service.save(CategoryApiMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable String id,@RequestBody CategoryRequest request) {
        service.update(id,CategoryApiMapper.toDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
