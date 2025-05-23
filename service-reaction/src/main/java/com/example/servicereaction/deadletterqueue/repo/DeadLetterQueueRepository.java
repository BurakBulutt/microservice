package com.example.servicereaction.deadletterqueue.repo;

import com.example.servicereaction.deadletterqueue.enums.MessageType;
import com.example.servicereaction.deadletterqueue.model.DeadLetterQueue;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeadLetterQueueRepository extends MongoRepository<DeadLetterQueue, ObjectId> {
    Page<DeadLetterQueue> findAllByType(MessageType type, Pageable pageable);
}
