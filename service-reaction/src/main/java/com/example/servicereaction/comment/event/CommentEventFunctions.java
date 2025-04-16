package com.example.servicereaction.comment.event;

import com.example.servicereaction.comment.service.CommentService;
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

    @Bean
    public Consumer<Set<String>> deleteComments(CommentService service) {
        return id -> {
            log.info("Delete comments message consumed: {}",id);
            service.deleteAllByTargetIdIn(id);
        };
    }

    @Bean
    public Consumer<Set<String>> deleteCommentsDlq() {
        return id -> {
            log.error("Delete comments message processing failed: {}, Message saving to document...",id);
        };
    }

    @RabbitListener(queues = "deleteComments.${spring.cloud.stream.default.group}.dlq")
    public void deleteCommentsDlq(Message message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        final Set<?> payload = mapper.readValue(message.getBody(), Set.class);
        log.error("Delete likes message processing failed: {}, Message saving to document...",payload);
    }

    @Bean
    public Consumer<String> deleteUserComments(CommentService service) {
        return id -> {
            log.info("Delete user comments message consumed: {}",id);
            service.deleteUserComments(id);
        };
    }

    @Bean
    public Consumer<String> deleteUserCommentsDlq() {
        return id -> {
            log.error("Delete user comments message processing failed: {}, Message saving to document...",id);
        };
    }

    @RabbitListener(queues = "deleteUserComments.${spring.cloud.stream.default.group}.dlq")
    public void deleteUserCommentsDlq(Message message) {
        final String payload = new String(message.getBody());
        log.error("Delete user likes message processing failed: {}, Message saving to document...",payload);
    }
}
