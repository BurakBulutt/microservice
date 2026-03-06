package com.example.serviceusers.auditlistener.service;


import com.example.serviceusers.auditlistener.enums.ProcessType;
import com.example.serviceusers.auditlistener.event.CreateEntityLogEvent;
import com.example.serviceusers.auditlistener.event.DeleteEntityLogEvent;
import com.example.serviceusers.auditlistener.event.UpdateEntityLogEvent;
import com.example.serviceusers.utilities.persistance.AbstractEntity;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntityLogListener {
    private final ApplicationEventPublisher publisher;

    @Bean("auditorProvider")
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
                return Optional.empty();
            }
            return Optional.of(authentication.getName());
        };
    }

    @PostPersist
    public void postPersist(AbstractEntity entity) {
        publisher.publishEvent(new CreateEntityLogEvent(ProcessType.CREATE, entity.getClass().getSimpleName(), entity.getId()));
    }

    @PostUpdate
    public void postUpdate(AbstractEntity entity) {
        publisher.publishEvent(new UpdateEntityLogEvent(ProcessType.UPDATE, entity.getClass().getSimpleName(), entity.getId()));
    }

    @PostRemove
    public void postRemove(AbstractEntity entity) {
        publisher.publishEvent(new DeleteEntityLogEvent(ProcessType.DELETE, entity.getClass().getSimpleName(), entity.getId()));
    }
}
