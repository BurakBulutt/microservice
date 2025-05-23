package com.example.servicereaction.auditlistener.event;


import com.example.servicereaction.auditlistener.enums.ProcessType;

public class CreateEntityLogEvent extends EntityLogEvent {
    public CreateEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
