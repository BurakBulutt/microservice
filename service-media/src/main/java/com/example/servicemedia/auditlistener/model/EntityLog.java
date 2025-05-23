package com.example.servicemedia.auditlistener.model;

import com.example.servicemedia.auditlistener.enums.ProcessType;
import jakarta.persistence.EntityListeners;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(EntityLog.TABLE)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@EntityListeners(AuditingEntityListener.class)
public class EntityLog {
    public static final String TABLE = "entity_log";

    @MongoId(FieldType.OBJECT_ID)
    private ObjectId id;

    @CreatedDate
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime updated;

    @LastModifiedBy
    private String user;

    private ProcessType process;

    private String entity;

    private String entityId;

    public EntityLog(ProcessType process, String entity,String entityId) {
        this.process = process;
        this.entity = entity;
        this.entityId = entityId;
    }
}
