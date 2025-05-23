package com.example.servicemedia.auditlistener.mapper;

import com.example.servicemedia.auditlistener.dto.EntityLogDto;
import com.example.servicemedia.auditlistener.model.EntityLog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityLogServiceMapper {

    public static EntityLogDto toDto(EntityLog entityLog) {
        return EntityLogDto.builder()
                .id(entityLog.getId())
                .process(entityLog.getProcess())
                .entity(entityLog.getEntity())
                .entityId(entityLog.getEntityId())
                .build();
    }
}
