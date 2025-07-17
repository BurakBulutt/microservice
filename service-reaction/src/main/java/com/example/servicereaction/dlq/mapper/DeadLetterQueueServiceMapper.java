package com.example.servicereaction.dlq.mapper;

import com.example.servicereaction.dlq.dto.DeadLetterQueueDto;
import com.example.servicereaction.dlq.model.DeadLetterQueue;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeadLetterQueueServiceMapper {

    public static DeadLetterQueueDto toDto(DeadLetterQueue deadLetterQueue) {
        return DeadLetterQueueDto.builder()
                .id(deadLetterQueue.getId())
                .type(deadLetterQueue.getType())
                .payload(deadLetterQueue.getPayload())
                .build();
    }

    public static DeadLetterQueue toEntity(DeadLetterQueue deadLetterQueue,DeadLetterQueueDto deadLetterQueueDto) {
        deadLetterQueue.setType(deadLetterQueueDto.getType());
        deadLetterQueue.setPayload(deadLetterQueue.getPayload());

        return deadLetterQueue;
    }
}
