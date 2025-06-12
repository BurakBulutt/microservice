package com.example.servicemedia.domain.category.api;

import com.example.servicemedia.domain.category.mapper.CategoryApiMapper;
import com.example.servicemedia.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<CategoryResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(CategoryApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("filter")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Page<CategoryResponse>> filter(Pageable pageable,@RequestParam(required = false) String query) {
        return ResponseEntity.ok(CategoryApiMapper.toPageResponse(service.filter(pageable,query)));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CategoryResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(CategoryApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("slug/{slug}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<CategoryResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(CategoryApiMapper.toResponse(service.getBySlug(slug)));
    }

    @GetMapping("count")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> save(@RequestBody @Valid CategoryRequest request) {
        service.save(CategoryApiMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<CategoryResponse> update(@PathVariable String id,@RequestBody @Valid CategoryRequest request) {
        service.update(id,CategoryApiMapper.toDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
