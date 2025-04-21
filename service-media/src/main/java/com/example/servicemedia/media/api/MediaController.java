package com.example.servicemedia.media.api;

import com.example.servicemedia.media.dto.MediaSourceDto;
import com.example.servicemedia.media.mapper.MediaApiMapper;
import com.example.servicemedia.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("medias")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService service;

    @GetMapping
    public ResponseEntity<Page<MediaResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(MediaApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("filter")
    public ResponseEntity<Page<MediaResponse>> filter(Pageable pageable,
                                                      @RequestParam(required = false) String content,
                                                      @RequestParam(required = false) String name) {
        return ResponseEntity.ok(MediaApiMapper.toPageResponse(service.filter(pageable, content, name)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(MediaApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<MediaResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(MediaApiMapper.toResponse(service.getBySlug(slug)));
    }

    @GetMapping("{mediaId}/media-sources")
    public ResponseEntity<List<MediaSourceResponse>> getMediaSources(@PathVariable String mediaId) {
        return ResponseEntity.ok(MediaApiMapper.toMediaSourceDataResponse(service.getMediaSourcesByMediaId(mediaId)));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody MediaRequest request) {
        service.save(MediaApiMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("{id}/increase-views")
    public ResponseEntity<Void> increaseNumberOfViews(@PathVariable String id) {
        service.increaseNumberOfViews(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@PathVariable String id, @RequestBody MediaRequest request) {
        service.update(id, MediaApiMapper.toDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{mediaId}/media-sources")
    public ResponseEntity<Void> updateMediaSources(@PathVariable String mediaId, @RequestBody MediaSourceRequest request) {
        service.updateMediaSources(mediaId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
