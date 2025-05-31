package com.example.servicemedia.domain.fansub.elasticsearch.event.listener;


import com.example.servicemedia.domain.fansub.elasticsearch.event.DeleteFansubEvent;
import com.example.servicemedia.domain.fansub.elasticsearch.event.SaveFansubEvent;
import com.example.servicemedia.domain.fansub.elasticsearch.model.ElasticFansub;
import com.example.servicemedia.domain.fansub.elasticsearch.repo.ElasticFansubRepository;
import com.example.servicemedia.domain.fansub.model.Fansub;
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
public class ElasticFansubEventListener {
    private final ElasticFansubRepository repository;
    private final ApplicationEventPublisher publisher;

    @PostPersist
    public void postPersist(Fansub entity) {
        publisher.publishEvent(new SaveFansubEvent(entity));
    }

    @PostUpdate
    public void postUpdate(Fansub entity) {
        publisher.publishEvent(new SaveFansubEvent(entity));
    }

    @PostRemove
    public void postRemove(Fansub entity) {
        publisher.publishEvent(new DeleteFansubEvent(entity.getId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = SaveFansubEvent.class,fallbackExecution = true)
    public void saveFansub(SaveFansubEvent event) {
        repository.save(ElasticEntityMapper.toElasticFansub(event.fansub()), RefreshPolicy.IMMEDIATE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = DeleteFansubEvent.class,fallbackExecution = true)
    public void deleteFansub(DeleteFansubEvent event) {
        ElasticFansub elasticFansub = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticFansub.class.getSimpleName(), event.id()));
        repository.delete(elasticFansub, RefreshPolicy.IMMEDIATE);
    }

}
