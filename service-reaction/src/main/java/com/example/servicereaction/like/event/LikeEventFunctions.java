package com.example.servicereaction.like.event;

import com.example.servicereaction.like.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class LikeEventFunctions {

    @Bean
    public Consumer<String> deleteLikes(LikeService service) {
        return id -> {
            log.info("Delete likes message consumed: {}",id);
            service.deleteLikesByTargetId(id);
        };
    }

    @Bean
    public Consumer<String> deleteLikesDlq() {
        return id -> {
            log.error("Delete likes message processing failed: {}, Message saving to document...",id);
        };
    }

    @Bean
    public Consumer<Set<String>> deleteLikesBulk(LikeService service) {
        return ids -> {
            log.info("Delete likes message consumed : {}",ids);
            service.deleteLikesByTargetIdIn(ids);
        };
    }

    @Bean
    public Consumer<Set<String>> deleteLikesBulkDlq() {
        return ids -> {
            log.error("Delete likes bulk message processing failed: {}, Message saving to document...",ids);
        };
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
}
