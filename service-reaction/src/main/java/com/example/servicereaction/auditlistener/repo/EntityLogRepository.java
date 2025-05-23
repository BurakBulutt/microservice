package com.example.servicereaction.auditlistener.repo;

import com.example.servicereaction.auditlistener.model.EntityLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntityLogRepository extends MongoRepository<EntityLog, ObjectId> {
}
