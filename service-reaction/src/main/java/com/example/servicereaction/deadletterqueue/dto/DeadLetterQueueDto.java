package com.example.servicereaction.deadletterqueue.dto;

import com.example.servicereaction.deadletterqueue.enums.MessageType;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class DeadLetterQueueDto {
    private ObjectId id;
    private MessageType type;
    private String payload;
}
