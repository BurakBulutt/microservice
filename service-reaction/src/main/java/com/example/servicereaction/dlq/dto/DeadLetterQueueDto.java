package com.example.servicereaction.dlq.dto;

import com.example.servicereaction.dlq.enums.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeadLetterQueueDto {
    private String id;
    private MessageType type;
    private String payload;
}
