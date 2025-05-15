package com.example.servicemedia.util;

import com.example.servicemedia.auditlistener.service.EntityLogListener;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@MappedSuperclass
@Getter
@EqualsAndHashCode(of = "id")
@EntityListeners({AuditingEntityListener.class, EntityLogListener.class})
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime created;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modified;
}
