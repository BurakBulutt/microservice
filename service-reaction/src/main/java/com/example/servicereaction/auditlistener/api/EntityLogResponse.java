package com.example.servicereaction.auditlistener.api;

import com.example.servicereaction.auditlistener.enums.ProcessType;
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
