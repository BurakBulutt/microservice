package com.example.servicemedia.auditlistener.api;

import com.example.servicemedia.auditlistener.enums.ProcessType;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class EntityLogResponse {
    private ObjectId id;
    private ProcessType process;
    private String entity;
}
