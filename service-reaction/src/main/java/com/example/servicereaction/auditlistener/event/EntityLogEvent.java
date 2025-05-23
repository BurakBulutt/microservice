package com.example.servicereaction.auditlistener.event;

import com.example.servicereaction.auditlistener.enums.ProcessType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityLogEvent {
    protected final ProcessType process;
    protected final String entity;
    protected final String entityId;
}
