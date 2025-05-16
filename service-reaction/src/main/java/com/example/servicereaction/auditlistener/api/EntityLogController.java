package com.example.servicereaction.auditlistener.api;


import com.example.servicereaction.auditlistener.dto.EntityLogDto;
import com.example.servicereaction.auditlistener.mapper.EntityLogApiMapper;
import com.example.servicereaction.auditlistener.service.EntityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reaction-entity-log")
@RequiredArgsConstructor
public class EntityLogController {
    private final EntityLogService service;

    @GetMapping
    public ResponseEntity<Page<EntityLogResponse>> getEntityLogs(@RequestParam(required = false) String entity,
                                                                 @RequestParam(required = false) String user,
                                                                 Pageable pageable) {
        Page<EntityLogDto> page;

        if (entity != null) {
            page = service.getAllByEntity(entity, pageable);
        }else if (user != null) {
            page = service.getAllByUser(user, pageable);
        } else {
            page = service.getAll(pageable);
        }

        return ResponseEntity.ok(EntityLogApiMapper.toPageResponse(page));
    }
}
