package com.example.servicereaction.domain.comment.event;

import com.example.servicereaction.domain.comment.service.CommentService;
import com.example.servicereaction.dlq.dto.DeadLetterQueueDto;
import com.example.servicereaction.dlq.enums.MessageType;
import com.example.servicereaction.dlq.service.DeadLetterQueueService;
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
public class CommentEventFunctions {
    private final DeadLetterQueueService deadLetterQueueService;

    @Bean
    public Consumer<Set<String>> deleteComments(CommentService service) {
        return ids -> {
            log.info("Delete comments message consumed: {}", ids);
            service.deleteAllByTargetIdIn(ids);
        };
    }

    @RabbitListener(queues = "deleteComments.${spring.cloud.stream.default.group}.dlq")
    public void deleteCommentsDlq(Message message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final Set<?> payload = mapper.readValue(message.getBody(), Set.class);
        log.warn("Delete likes message processing failed: {}, Message saving to document...", payload);
        deadLetterQueueService.save(DeadLetterQueueDto.builder()
                .type(MessageType.DELETE_COMMENTS)
                .payload(payload.toString())
                .build());
    }

    @Bean
    public Consumer<String> deleteUserComments(CommentService service) {
        return id -> {
            log.info("Delete user comments message consumed: {}", id);
            service.deleteUserComments(id);
        };
    }

    @RabbitListener(queues = "deleteUserComments.${spring.cloud.stream.default.group}.dlq")
    public void deleteUserCommentsDlq(Message message) {
        final String payload = new String(message.getBody());
        log.warn("Delete user likes message processing failed: {}, Message saving to document...", payload);
        deadLetterQueueService.save(DeadLetterQueueDto.builder()
                .type(MessageType.DELETE_USER_COMMENTS)
                .payload(payload)
                .build());
    }
}
