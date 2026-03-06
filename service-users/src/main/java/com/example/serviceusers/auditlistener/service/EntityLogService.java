package com.example.serviceusers.auditlistener.service;

import com.example.serviceusers.auditlistener.dto.EntityLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityLogService {
    Page<EntityLogDto> getAll(Pageable pageable);
}
