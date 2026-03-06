package com.example.serviceusers.auditlistener.event;

import com.example.serviceusers.auditlistener.enums.ProcessType;

public class UpdateEntityLogEvent extends EntityLogEvent {
    public UpdateEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
