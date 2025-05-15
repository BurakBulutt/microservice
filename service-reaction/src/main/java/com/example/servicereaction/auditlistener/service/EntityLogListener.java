package com.example.servicereaction.auditlistener.service;

import com.example.servicereaction.auditlistener.enums.ProcessType;
import com.example.servicereaction.auditlistener.model.EntityLog;
import com.example.servicereaction.auditlistener.repo.EntityLogRepository;
import com.example.servicereaction.util.AbstractEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
public class EntityLogListener {
    private final EntityLogRepository repository;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(MDC.get("user"));
    }

    @PostPersist
    public void postPersist(AbstractEntity entity) {
        repository.save(new EntityLog(ProcessType.CREATE,entity.getClass().getSimpleName()));
    }

    @PostUpdate
    public void postUpdate(AbstractEntity entity) {
        repository.save(new EntityLog(ProcessType.UPDATE,entity.getClass().getSimpleName()));
    }

    @PostRemove
    public void postRemove(AbstractEntity entity) {
        repository.save(new EntityLog(ProcessType.DELETE,entity.getClass().getSimpleName()));
    }
    
}
