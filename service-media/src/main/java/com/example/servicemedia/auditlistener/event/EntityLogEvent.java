package com.example.servicemedia.auditlistener.event;

import com.example.servicemedia.auditlistener.enums.ProcessType;
import lombok.*;

@RequiredArgsConstructor
@Getter
public class EntityLogEvent {
    protected final ProcessType process;
    protected final String entity;
    protected final String entityId;
}
