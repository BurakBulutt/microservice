package com.example.servicemedia.media.api;

import com.example.servicemedia.media.mapper.MediaApiMapper;
import com.example.servicemedia.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/medias")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService service;

    @GetMapping
    public ResponseEntity<Page<MediaResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(MediaApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("new-media")
    public ResponseEntity<Page<MediaResponse>> getNewMedia(Pageable pageable) {
        return ResponseEntity.ok(MediaApiMapper.toPageResponse(service.getNewMedias(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(MediaApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<MediaResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(MediaApiMapper.toResponse(service.getBySlug(slug)));
    }

    @PostMapping
    public ResponseEntity<MediaResponse> save(@RequestBody MediaRequest request) {
        return ResponseEntity.ok(MediaApiMapper.toResponse(service.save(MediaApiMapper.toDto(request))));
    }

    @PutMapping("{id}")
    public ResponseEntity<MediaResponse> update(@PathVariable String id,@RequestBody MediaRequest request) {
        return ResponseEntity.ok(MediaApiMapper.toResponse(service.update(id,MediaApiMapper.toDto(request))));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
