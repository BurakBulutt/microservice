package com.example.servicemedia.domain.fansub.api;

import com.example.servicemedia.domain.fansub.mapper.FansubApiMapper;
import com.example.servicemedia.domain.fansub.service.FansubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("fansubs")
@RequiredArgsConstructor
public class FansubController {
    private final FansubService service;

    @GetMapping
    public ResponseEntity<Page<FansubResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(FansubApiMapper.toPageResponse(service.getAll(pageable)));
    }

    @GetMapping("filter")
    public ResponseEntity<Page<FansubResponse>> filter(Pageable pageable) {
        return ResponseEntity.ok(FansubApiMapper.toPageResponse(service.filter(pageable)));
    }

    @GetMapping("{id}")
    public ResponseEntity<FansubResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(FansubApiMapper.toResponse(service.getById(id)));
    }

    @GetMapping("count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid  FansubRequest request) {
        service.save(FansubApiMapper.toDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<FansubResponse> update(@PathVariable String id, @RequestBody  @Valid FansubRequest request) {
        service.update(id, FansubApiMapper.toDto(request));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
