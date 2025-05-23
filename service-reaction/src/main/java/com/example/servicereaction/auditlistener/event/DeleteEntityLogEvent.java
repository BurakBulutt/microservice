package com.example.servicereaction.auditlistener.event;


import com.example.servicereaction.auditlistener.enums.ProcessType;

public class DeleteEntityLogEvent extends EntityLogEvent {
    public DeleteEntityLogEvent(ProcessType process, String entity, String entityId) {
        super(process, entity, entityId);
    }
}
