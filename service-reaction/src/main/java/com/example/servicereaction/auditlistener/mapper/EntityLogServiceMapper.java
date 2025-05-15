package com.example.servicereaction.auditlistener.mapper;

import com.example.servicereaction.auditlistener.dto.EntityLogDto;
import com.example.servicereaction.auditlistener.model.EntityLog;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityLogServiceMapper {

    public static EntityLogDto toDto(EntityLog entityLog) {
        return EntityLogDto.builder()
                .id(entityLog.getId())
                .process(entityLog.getProcess())
                .entity(entityLog.getEntity())
                .build();
    }
}
