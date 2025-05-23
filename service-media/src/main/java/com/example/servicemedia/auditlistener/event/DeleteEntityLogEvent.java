package com.example.servicemedia.auditlistener.event;

import com.example.servicemedia.auditlistener.enums.ProcessType;

public class DeleteEntityLogEvent extends EntityLogEvent {
    public DeleteEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
