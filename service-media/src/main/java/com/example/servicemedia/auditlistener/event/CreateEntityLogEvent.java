package com.example.servicemedia.auditlistener.event;

import com.example.servicemedia.auditlistener.enums.ProcessType;

public class CreateEntityLogEvent extends EntityLogEvent {
    public CreateEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
