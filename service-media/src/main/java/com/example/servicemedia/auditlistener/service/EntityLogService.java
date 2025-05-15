package com.example.servicemedia.auditlistener.service;

import com.example.servicemedia.auditlistener.dto.EntityLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityLogService {
    Page<EntityLogDto> getAll(Pageable pageable);
    Page<EntityLogDto> getAllByUser(String user, Pageable pageable);
    Page<EntityLogDto> getAllByEntity(String entity, Pageable pageable);
}
