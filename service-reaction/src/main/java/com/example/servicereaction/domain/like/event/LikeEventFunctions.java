package com.example.servicereaction.domain.like.event;

import com.example.servicereaction.dlq.dto.DeadLetterQueueDto;
import com.example.servicereaction.dlq.enums.MessageType;
import com.example.servicereaction.dlq.service.DeadLetterQueueService;
import com.example.servicereaction.domain.like.service.LikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LikeEventFunctions {
    private final DeadLetterQueueService deadLetterQueueService;

    @Bean
    public Consumer<Set<String>> deleteLikes(LikeService service) {
        return ids -> {
            log.info("Delete likes message consumed: {}", ids);
            service.deleteLikesByTargetIdIn(ids);
        };
    }

    @RabbitListener(queues = "deleteLikes.${spring.cloud.stream.default.group}.dlq")
    public void deleteLikesDlq(Message message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final Set<?> payload = mapper.readValue(message.getBody(), Set.class);
        log.warn("Delete likes message processing failed: {}, Message saving to document...", payload);
        deadLetterQueueService.save(DeadLetterQueueDto.builder()
                .type(MessageType.DELETE_LIKES)
                .payload(payload.toString())
                .build());
    }

    @Bean
    public Consumer<String> deleteUserLikes(LikeService service) {
        return id -> {
            log.info("Delete user likes message consumed: {}", id);
            service.deleteUserLikes(id);
        };
    }

    @RabbitListener(queues = "deleteUserLikes.${spring.cloud.stream.default.group}.dlq")
    public void deleteUserLikesDlq(Message message) {
        final String payload = new String(message.getBody());
        log.warn("Delete user likes message processing failed: {}, Message saving to document...", payload);
        deadLetterQueueService.save(DeadLetterQueueDto.builder()
                .type(MessageType.DELETE_USER_LIKES)
                .payload(payload)
                .build());
    }
}
