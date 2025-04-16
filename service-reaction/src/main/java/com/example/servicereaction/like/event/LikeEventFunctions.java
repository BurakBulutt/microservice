package com.example.servicereaction.like.event;

import com.example.servicereaction.like.service.LikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class LikeEventFunctions {

    @Bean
    public Consumer<Set<String>> deleteLikes(LikeService service) {
        return id -> {
            log.info("Delete likes message consumed: {}",id);
            service.deleteLikesByTargetIdIn(id);
        };
    }

    @Bean
    public Consumer<Set<String>> deleteLikesDlq() {
        return ids -> {
            log.error("Delete likes message processing failed: {}, Message saving to document...",ids);
        };
    }

    @RabbitListener(queues = "deleteLikes.${spring.cloud.stream.default.group}.dlq")
    public void deleteLikesDlq(Message message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Set<?> payload = mapper.readValue(message.getBody(), Set.class);
        log.error("Delete likes message processing failed: {}, Message saving to document...",payload);
    }

    @Bean
    public Consumer<String> deleteUserLikes(LikeService service) {
        return id -> {
            log.info("Delete user likes message consumed: {}",id);
            service.deleteUserLikes(id);
        };
    }

    @Bean
    public Consumer<String> deleteUserLikesDlq() {
        return id -> {
            log.error("Delete user likes message processing failed: {}, Message saving to document...",id);
        };
    }

    @RabbitListener(queues = "deleteUserLikes.${spring.cloud.stream.default.group}.dlq")
    public void deleteUserLikesDlq(Message message) {
        String payload = new String(message.getBody());
        log.error("Delete user likes message processing failed: {}, Message saving to document...",payload);
    }
}
