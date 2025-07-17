package com.example.servicereaction.dlq.service;

import com.example.servicereaction.dlq.dto.DeadLetterQueueDto;
import com.example.servicereaction.dlq.enums.MessageType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeadLetterQueueService {
    Page<DeadLetterQueueDto> getAll(Pageable pageable);
    Page<DeadLetterQueueDto> getAllByType(MessageType type, Pageable pageable);
    void save(DeadLetterQueueDto deadLetterQueueDto);
    void delete(String id);
}
