package com.example.servicemedia.domain.xml.elasticsearch.event.listener;

import com.example.servicemedia.domain.xml.elasticsearch.event.DeleteXmlDefinitionEvent;
import com.example.servicemedia.domain.xml.elasticsearch.event.SaveXmlDefinitionEvent;
import com.example.servicemedia.domain.xml.elasticsearch.model.ElasticXmlDefinition;
import com.example.servicemedia.domain.xml.elasticsearch.repo.ElasticXmlDefinitionRepository;
import com.example.servicemedia.domain.xml.model.XmlDefinition;
import com.example.servicemedia.elasticsearch.ElasticEntityMapper;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticXmlDefinitionEventListener {
    private final ElasticXmlDefinitionRepository repository;
    private final ApplicationEventPublisher publisher;

    @PostPersist
    public void postPersist(XmlDefinition entity) {
        publisher.publishEvent(new SaveXmlDefinitionEvent(entity));
    }

    @PostUpdate
    public void postUpdate(XmlDefinition entity) {
        publisher.publishEvent(new SaveXmlDefinitionEvent(entity));
    }

    @PostRemove
    public void postRemove(XmlDefinition entity) {
        publisher.publishEvent(new DeleteXmlDefinitionEvent(entity.getId()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = SaveXmlDefinitionEvent.class,fallbackExecution = true)
    public void saveXmlDefinition(SaveXmlDefinitionEvent event) {
        repository.save(ElasticEntityMapper.toElasticXmlDefinition(event.xmlDefinition()), RefreshPolicy.IMMEDIATE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,value = DeleteXmlDefinitionEvent.class,fallbackExecution = true)
    public void deleteXmlDefinition(DeleteXmlDefinitionEvent event) {
        ElasticXmlDefinition elasticXmlDefinition = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticXmlDefinition.class.getSimpleName(), event.id()));
        repository.delete(elasticXmlDefinition, RefreshPolicy.IMMEDIATE);
    }
}
