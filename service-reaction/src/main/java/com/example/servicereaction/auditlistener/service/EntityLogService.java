package com.example.servicereaction.auditlistener.service;

import com.example.servicereaction.auditlistener.dto.EntityLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityLogService {
    Page<EntityLogDto> getAll(Pageable pageable);
}
