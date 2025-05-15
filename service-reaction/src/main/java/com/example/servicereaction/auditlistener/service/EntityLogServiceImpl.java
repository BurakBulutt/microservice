package com.example.servicereaction.auditlistener.service;

import com.example.servicereaction.auditlistener.dto.EntityLogDto;
import com.example.servicereaction.auditlistener.mapper.EntityLogServiceMapper;
import com.example.servicereaction.auditlistener.repo.EntityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
public class EntityLogServiceImpl implements EntityLogService {
    private final EntityLogRepository repository;

    @Override
    public Page<EntityLogDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(EntityLogServiceMapper::toDto);
    }

    @Override
    public Page<EntityLogDto> getAllByUser(String user, Pageable pageable) {
        return repository.findAllByUser(user,pageable).map(EntityLogServiceMapper::toDto);
    }

    @Override
    public Page<EntityLogDto> getAllByEntity(String entity, Pageable pageable) {
        return repository.findAllByEntityContainingIgnoreCase(entity,pageable).map(EntityLogServiceMapper::toDto);
    }
}
