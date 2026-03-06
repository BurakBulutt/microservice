package com.example.serviceusers.auditlistener.event;


import com.example.serviceusers.auditlistener.enums.ProcessType;

public class CreateEntityLogEvent extends EntityLogEvent {
    public CreateEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
