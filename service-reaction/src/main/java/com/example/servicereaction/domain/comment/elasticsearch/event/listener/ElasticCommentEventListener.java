package com.example.servicereaction.domain.comment.elasticsearch.event.listener;


import com.example.servicereaction.domain.comment.dto.CommentDto;
import com.example.servicereaction.domain.comment.elasticsearch.event.CreateCommentEvent;
import com.example.servicereaction.domain.comment.elasticsearch.event.DeleteCommentEvent;
import com.example.servicereaction.domain.comment.elasticsearch.event.UpdateCommentEvent;
import com.example.servicereaction.domain.comment.elasticsearch.model.ElasticComment;
import com.example.servicereaction.domain.comment.elasticsearch.repo.ElasticCommentRepository;
import com.example.servicereaction.util.exception.BaseException;
import com.example.servicereaction.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ElasticCommentEventListener {
    private final ElasticCommentRepository repository;

    @EventListener(CreateCommentEvent.class)
    public void createContent(CreateCommentEvent event) {
        repository.save(toEntity(new ElasticComment(),event.comment()), RefreshPolicy.IMMEDIATE);
    }

    @EventListener(UpdateCommentEvent.class)
    public void updateContent(UpdateCommentEvent event) {
        ElasticComment elasticComment = repository.findById(event.comment().getId()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticComment.class.getSimpleName(), event.comment().getId()));
        repository.save(toEntity(elasticComment,event.comment()), RefreshPolicy.IMMEDIATE);
    }

    @EventListener(DeleteCommentEvent.class)
    public void deleteContent(DeleteCommentEvent event) {
        ElasticComment elasticComment = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticComment.class.getSimpleName(), event.id()));
        repository.delete(elasticComment, RefreshPolicy.IMMEDIATE);
    }

    private ElasticComment toEntity(ElasticComment entity, CommentDto dto) {
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated().atOffset(ZoneOffset.UTC));
        entity.setCommentType(dto.getCommentType().name());
        entity.setTargetType(dto.getTargetType().name());
        entity.setTargetId(dto.getTargetId());
        entity.setUserId(dto.getUserId());

        if (dto.getParent() != null) {
            entity.setParentId(dto.getParent().getId());
        }

        return entity;
    }

}
