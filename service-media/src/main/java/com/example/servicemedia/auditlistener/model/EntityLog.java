package com.example.servicemedia.auditlistener.model;

import com.example.servicemedia.auditlistener.enums.ProcessType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name= EntityLog.TABLE)
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
    private LocalDateTime modified;

    @LastModifiedBy
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ProcessType process;

    @Column(nullable=false)
    private String entity;

    @Column(nullable=false)
    private String entityId;

    public EntityLog(ProcessType process, String entity,String entityId) {
        this.process = process;
        this.entity = entity;
        this.entityId = entityId;
    }
}
