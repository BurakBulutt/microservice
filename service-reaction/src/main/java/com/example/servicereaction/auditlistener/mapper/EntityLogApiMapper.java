package com.example.servicereaction.auditlistener.mapper;

import com.example.servicereaction.auditlistener.api.EntityLogResponse;
import com.example.servicereaction.auditlistener.dto.EntityLogDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityLogApiMapper {

    public static EntityLogResponse toDto(EntityLogDto entityChanges) {
        return EntityLogResponse.builder()
                .id(entityChanges.getId())
                .process(entityChanges.getProcess())
                .entity(entityChanges.getEntity())
                .entityId(entityChanges.getEntityId())
                .user(entityChanges.getUser())
                .build();
    }

    public static Page<EntityLogResponse> toPageResponse(Page<EntityLogDto> page) {
        return page.map(EntityLogApiMapper::toDto);
    }
}
