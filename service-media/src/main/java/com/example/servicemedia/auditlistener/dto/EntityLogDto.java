package com.example.servicemedia.auditlistener.dto;

import com.example.servicemedia.auditlistener.enums.ProcessType;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class EntityLogDto {
    private ObjectId id;
    private ProcessType process;
    private String entity;
}
