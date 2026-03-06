package com.example.serviceusers.auditlistener.service;


import com.example.serviceusers.auditlistener.dto.EntityLogDto;
import com.example.serviceusers.auditlistener.enums.ProcessType;
import com.example.serviceusers.auditlistener.event.CreateEntityLogEvent;
import com.example.serviceusers.auditlistener.event.DeleteEntityLogEvent;
import com.example.serviceusers.auditlistener.event.UpdateEntityLogEvent;
import com.example.serviceusers.auditlistener.mapper.EntityLogServiceMapper;
import com.example.serviceusers.auditlistener.model.EntityLog;
import com.example.serviceusers.auditlistener.repo.EntityLogRepository;
import com.example.serviceusers.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityLogServiceImpl implements EntityLogService {
    private final EntityLogRepository repository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<EntityLogDto> getAll(Pageable pageable) {
        log.info("Getting all entity logs");
        return repository.findAll(pageable).map(this::toDto);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = CreateEntityLogEvent.class)
    public void createLog(CreateEntityLogEvent log) {
        repository.save(new EntityLog(ProcessType.CREATE,log.getEntity(),log.getEntityId()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = UpdateEntityLogEvent.class)
    public void updateLog(UpdateEntityLogEvent log) {
        repository.save(new EntityLog(ProcessType.UPDATE,log.getEntity(),log.getEntityId()));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = DeleteEntityLogEvent.class)
    public void deleteLog(DeleteEntityLogEvent log) {
        repository.save(new EntityLog(ProcessType.DELETE,log.getEntity(),log.getEntityId()));
    }

    private EntityLogDto toDto(EntityLog entityLog) {
        EntityLogDto dto = EntityLogServiceMapper.toDto(entityLog);

        if (entityLog.getUserId() == null || entityLog.getUserId().isBlank()) {
            return dto;
        }

        com.example.serviceusers.domain.user.dto.UserDto user = userService.getById(entityLog.getUserId());
        dto.setUser(user);


        return dto;
    }
}
