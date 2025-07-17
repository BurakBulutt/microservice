package com.example.servicereaction.dlq.repo;

import com.example.servicereaction.dlq.enums.MessageType;
import com.example.servicereaction.dlq.model.DeadLetterQueue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadLetterQueueRepository extends JpaRepository<DeadLetterQueue, String> {
    Page<DeadLetterQueue> findAllByType(MessageType type, Pageable pageable);
}
