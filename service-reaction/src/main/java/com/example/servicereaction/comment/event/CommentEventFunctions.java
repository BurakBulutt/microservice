package com.example.servicereaction.comment.event;

import com.example.servicereaction.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CommentEventFunctions {

    @Bean
    public Consumer<String> deleteComments(CommentService service) {
        return id -> {
            log.info("Delete comments message consumed: {}",id);
            service.deleteAllByTargetId(id);
        };
    }

    @Bean
    public Consumer<String> deleteCommentsDlq() {
        return id -> {
            log.error("Delete comments message processing failed: {}, Message saving to document...",id);
        };
    }

    @Bean
    public Consumer<Set<String>> deleteCommentsBulk(CommentService service) {
        return ids -> {
            log.info("Deleting comments bulk message consumed: {}",ids);
            service.deleteAllByTargetIdIn(ids);
        };
    }

    @Bean
    public Consumer<Set<String>> deleteCommentsBulkDlq() {
        return ids -> {
            log.error("Delete comments bulk message processing failed: {}, Message saving to document...",ids);
        };
    }

    @Bean
    public Consumer<String> deleteUserComments(CommentService service) {
        return id -> {
            log.info("Deleting user comments message: {}",id);
            service.deleteUserComments(id);
        };
    }

    @Bean
    public Consumer<String> deleteUserCommentsDlq() {
        return id -> {
            log.error("Delete user comments message processing failed: {}, Message saving to document...",id);
        };
    }
}
