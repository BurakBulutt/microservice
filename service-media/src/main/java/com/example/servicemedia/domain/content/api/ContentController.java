package com.example.servicemedia.domain.content.api;

import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.domain.content.mapper.ContentApiMapper;
import com.example.servicemedia.domain.content.service.ContentService;
import com.example.servicemedia.feign.like.LikeType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("contents")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Page<ContentResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ContentApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("filter")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Page<ContentResponse>> filter(Pageable pageable,
                                                        @RequestParam(required = false) String category,
                                                        @RequestParam(required = false) String query,
                                                        @RequestParam(required = false) LocalDate firstDate,
                                                        @RequestParam(required = false) LocalDate lastDate,
                                                        @RequestParam(required = false) ContentType type) {
        return ResponseEntity.ok(ContentApiMapper.toPageResponse(service.filter(pageable, category,query,firstDate,lastDate,type)));
    }

    @GetMapping("search")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<ContentSearchResponse>> filter(@RequestParam String query) {
        return ResponseEntity.ok(ContentApiMapper.toSearchResponseList(service.search(query)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ContentResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(ContentApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ContentResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ContentApiMapper.toResponse(service.getBySlug(slug)));
    }

    @GetMapping("top-content-by")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ContentNameResponse> getByTop(@RequestParam LikeType likeType) {
        return ResponseEntity.ok(ContentApiMapper.toNameResponse(service.getByTop(likeType)));
    }

    @GetMapping("count")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Long> getCount() {
        return ResponseEntity.ok(service.getCount());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> save(@RequestBody @Valid ContentRequest request) {
        service.save(ContentApiMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody @Valid ContentRequest request) {
        service.update(id, ContentApiMapper.toDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
