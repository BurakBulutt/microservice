package com.example.servicemedia.auditlistener.service;

import com.example.servicemedia.auditlistener.dto.EntityLogDto;
import com.example.servicemedia.auditlistener.mapper.EntityLogServiceMapper;
import com.example.servicemedia.auditlistener.model.EntityLog;
import com.example.servicemedia.auditlistener.repo.EntityLogRepository;
import com.example.servicemedia.feign.user.UserFeignClient;
import com.example.servicemedia.feign.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
@Slf4j
public class EntityLogServiceImpl implements EntityLogService {
    private final EntityLogRepository repository;
    private final UserFeignClient userFeignClient;

    @Override
    public Page<EntityLogDto> getAll(Pageable pageable) {
        log.info("Getting all entity logs");
        return repository.findAll(pageable).map(this::toDto);
    }

    private EntityLogDto toDto(EntityLog entityLog) {
        EntityLogDto dto = EntityLogServiceMapper.toDto(entityLog);

        if (entityLog.getUser() == null || entityLog.getUser().isBlank()) {
            return dto;
        }

        ResponseEntity<UserResponse> response = userFeignClient.getById(entityLog.getUser());

        if (response.hasBody()) {
            dto.setUser(response.getBody());
        }

        return dto;
    }
}
