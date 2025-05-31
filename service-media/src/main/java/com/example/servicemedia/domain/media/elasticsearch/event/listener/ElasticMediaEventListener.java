package com.example.servicemedia.domain.media.elasticsearch.event.listener;

import com.example.servicemedia.domain.media.elasticsearch.event.DeleteMediaEvent;
import com.example.servicemedia.domain.media.elasticsearch.event.SaveMediaEvent;
import com.example.servicemedia.domain.media.elasticsearch.model.ElasticMedia;
import com.example.servicemedia.domain.media.elasticsearch.repo.ElasticMediaRepository;
import com.example.servicemedia.domain.media.model.Media;
import com.example.servicemedia.elasticsearch.ElasticEntityMapper;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
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
public class ElasticMediaEventListener {
    private final ElasticMediaRepository repository;
    private final ApplicationEventPublisher publisher;

    @PostPersist
    public void postPersist(Media entity) {
        publisher.publishEvent(new SaveMediaEvent(entity));
    }

    @PostUpdate
    public void postUpdate(Media entity) {
        publisher.publishEvent(new SaveMediaEvent(entity));
    }

    @PostRemove
    public void postRemove(Media entity) {
        publisher.publishEvent(new DeleteMediaEvent(entity.getId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = SaveMediaEvent.class,fallbackExecution = true)
    public void saveMedia(SaveMediaEvent event) {
        repository.save(ElasticEntityMapper.toElasticMedia(event.media()), RefreshPolicy.IMMEDIATE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = DeleteMediaEvent.class,fallbackExecution = true)
    public void deleteMedia(DeleteMediaEvent event) {
        ElasticMedia elasticMedia = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticMedia.class.getSimpleName(), event.id()));
        repository.delete(elasticMedia, RefreshPolicy.IMMEDIATE);
    }

}
