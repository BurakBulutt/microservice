package com.example.servicemedia.domain.category.elasticsearch.event.listener;


import com.example.servicemedia.domain.category.elasticsearch.event.DeleteCategoryEvent;
import com.example.servicemedia.domain.category.elasticsearch.event.SaveCategoryEvent;
import com.example.servicemedia.domain.category.elasticsearch.model.ElasticCategory;
import com.example.servicemedia.domain.category.elasticsearch.repo.ElasticCategoryRepository;
import com.example.servicemedia.domain.category.model.Category;
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
public class ElasticCategoryEventListener {
    private final ElasticCategoryRepository repository;
    private final ApplicationEventPublisher publisher;

    @PostPersist
    public void postPersist(Category entity) {
        publisher.publishEvent(new SaveCategoryEvent(entity));
    }

    @PostUpdate
    public void postUpdate(Category entity) {
        publisher.publishEvent(new SaveCategoryEvent(entity));
    }

    @PostRemove
    public void postRemove(Category entity) {
        publisher.publishEvent(new DeleteCategoryEvent(entity.getId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = SaveCategoryEvent.class,fallbackExecution = true)
    public void saveCategory(SaveCategoryEvent event) {
        repository.save(ElasticEntityMapper.toElasticCategory(event.category()), RefreshPolicy.IMMEDIATE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = DeleteCategoryEvent.class,fallbackExecution = true)
    public void deleteCategory(DeleteCategoryEvent event) {
        ElasticCategory elasticCategory = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticCategory.class.getSimpleName(), event.id()));
        repository.delete(elasticCategory, RefreshPolicy.IMMEDIATE);
    }
}
