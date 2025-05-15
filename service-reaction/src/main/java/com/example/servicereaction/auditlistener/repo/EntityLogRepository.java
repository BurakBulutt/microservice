package com.example.servicereaction.auditlistener.repo;

import com.example.servicereaction.auditlistener.model.EntityLog;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntityLogRepository extends MongoRepository<EntityLog, ObjectId> {
    Page<EntityLog> findAllByEntityContainingIgnoreCase(String entity, Pageable pageable);

    Page<EntityLog> findAllByUser(String user, Pageable pageable);
}
