package com.example.servicereaction.auditlistener.model;

import com.example.servicereaction.auditlistener.enums.ProcessType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = EntityLog.TABLE)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class EntityLog {
    public static final String TABLE = "entity_log";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreatedDate
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime updated;

    @LastModifiedBy
    private String userId;

    private ProcessType process;

    private String entity;

    private String entityId;

    public EntityLog(ProcessType process, String entity,String entityId) {
        this.process = process;
        this.entity = entity;
        this.entityId = entityId;
    }
}
