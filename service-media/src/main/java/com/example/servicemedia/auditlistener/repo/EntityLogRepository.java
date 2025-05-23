package com.example.servicemedia.auditlistener.repo;

import com.example.servicemedia.auditlistener.model.EntityLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntityLogRepository extends MongoRepository<EntityLog, ObjectId> {
}
