package com.example.servicereaction.domain.comment.elasticsearch.event.listener;


import com.example.servicereaction.domain.comment.elasticsearch.event.SaveCommentEvent;
import com.example.servicereaction.domain.comment.elasticsearch.event.DeleteCommentEvent;
import com.example.servicereaction.domain.comment.elasticsearch.model.ElasticComment;
import com.example.servicereaction.domain.comment.elasticsearch.repo.ElasticCommentRepository;
import com.example.servicereaction.domain.comment.model.Comment;
import com.example.servicereaction.util.exception.BaseException;
import com.example.servicereaction.util.exception.MessageResource;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static com.example.servicereaction.elasticsearch.ElasticEntityMapper.toElasticComment;

@Component
@RequiredArgsConstructor
public class ElasticCommentEventListener {
    private final ElasticCommentRepository repository;
    private final ApplicationEventPublisher publisher;

    @PostPersist
    public void postPersist(Comment entity) {
        publisher.publishEvent(new SaveCommentEvent(entity));
    }

    @PostUpdate
    public void postUpdate(Comment entity) {
        publisher.publishEvent(new SaveCommentEvent(entity));
    }

    @PostRemove
    public void postRemove(Comment entity) {
        publisher.publishEvent(new DeleteCommentEvent(entity.getId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = SaveCommentEvent.class,fallbackExecution = true)
    public void saveContent(SaveCommentEvent event) {
        repository.save(toElasticComment(event.comment()), RefreshPolicy.IMMEDIATE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = DeleteCommentEvent.class,fallbackExecution = true)
    public void deleteContent(DeleteCommentEvent event) {
        ElasticComment elasticComment = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticComment.class.getSimpleName(), event.id()));
        repository.delete(elasticComment, RefreshPolicy.IMMEDIATE);
    }
}
