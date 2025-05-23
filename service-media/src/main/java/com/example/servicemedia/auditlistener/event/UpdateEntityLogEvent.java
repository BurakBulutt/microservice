package com.example.servicemedia.auditlistener.event;

import com.example.servicemedia.auditlistener.enums.ProcessType;

public class UpdateEntityLogEvent extends EntityLogEvent {
    public UpdateEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
