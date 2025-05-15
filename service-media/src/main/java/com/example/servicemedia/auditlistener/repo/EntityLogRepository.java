package com.example.servicemedia.auditlistener.repo;

import com.example.servicemedia.auditlistener.model.EntityLog;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntityLogRepository extends MongoRepository<EntityLog, ObjectId> {
    Page<EntityLog> findAllByEntityContainingIgnoreCase(String entity, Pageable pageable);

    Page<EntityLog> findAllByUser(String user, Pageable pageable);
}
