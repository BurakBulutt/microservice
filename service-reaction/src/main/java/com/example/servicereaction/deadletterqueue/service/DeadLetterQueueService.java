package com.example.servicereaction.deadletterqueue.service;

import com.example.servicereaction.deadletterqueue.dto.DeadLetterQueueDto;
import com.example.servicereaction.deadletterqueue.enums.MessageType;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeadLetterQueueService {
    Page<DeadLetterQueueDto> getAll(Pageable pageable);
    Page<DeadLetterQueueDto> getAllByType(MessageType type, Pageable pageable);
    void save(DeadLetterQueueDto deadLetterQueueDto);
    void delete(ObjectId id);
}
