package com.example.serviceusers.domain.user.elasticsearch.event.listener;


import com.example.serviceusers.domain.user.elasticsearch.event.DeleteUserEvent;
import com.example.serviceusers.domain.user.elasticsearch.event.SaveUserEvent;
import com.example.serviceusers.domain.user.elasticsearch.model.ElasticUser;
import com.example.serviceusers.domain.user.elasticsearch.repo.ElasticUserRepository;
import com.example.serviceusers.domain.user.model.User;
import com.example.serviceusers.elasticsearch.ElasticEntityMapper;
import com.example.serviceusers.utilities.exception.BaseException;
import com.example.serviceusers.utilities.exception.MessageResource;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
public class ElasticUserEventListener {
    private final ElasticUserRepository repository;
    private final ApplicationEventPublisher publisher;

    @PostPersist
    public void postPersist(User entity) {
        publisher.publishEvent(new SaveUserEvent(entity));
    }

    @PostUpdate
    public void postUpdate(User entity) {
        publisher.publishEvent(new SaveUserEvent(entity));
    }

    @PostRemove
    public void postRemove(User entity) {
        publisher.publishEvent(new DeleteUserEvent(entity.getId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = SaveUserEvent.class,fallbackExecution = true)
    public void saveUser(SaveUserEvent event) {
        repository.save(ElasticEntityMapper.toElasticUser(event.user()), RefreshPolicy.NONE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = DeleteUserEvent.class,fallbackExecution = true)
    public void deleteUser(DeleteUserEvent event) {
        ElasticUser elasticUser = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticUser.class.getSimpleName(), event.id()));
        repository.delete(elasticUser, RefreshPolicy.NONE);
    }
}
