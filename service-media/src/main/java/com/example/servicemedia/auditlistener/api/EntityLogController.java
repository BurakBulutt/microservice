package com.example.servicemedia.auditlistener.api;

import com.example.servicemedia.auditlistener.mapper.EntityLogApiMapper;
import com.example.servicemedia.auditlistener.service.EntityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("media-entity-log")
@RequiredArgsConstructor
public class EntityLogController {
    private final EntityLogService service;

    @GetMapping
    public ResponseEntity<Page<EntityLogResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(EntityLogApiMapper.toPageResponse(service.getAll(pageable)));
    }
}
