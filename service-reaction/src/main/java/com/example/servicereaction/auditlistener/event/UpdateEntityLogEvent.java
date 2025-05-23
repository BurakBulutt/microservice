package com.example.servicereaction.auditlistener.event;


import com.example.servicereaction.auditlistener.enums.ProcessType;

public class UpdateEntityLogEvent extends EntityLogEvent {
    public UpdateEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
