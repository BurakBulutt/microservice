package com.example.serviceusers.auditlistener.event;

import com.example.serviceusers.auditlistener.enums.ProcessType;

public class DeleteEntityLogEvent extends EntityLogEvent {
    public DeleteEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
