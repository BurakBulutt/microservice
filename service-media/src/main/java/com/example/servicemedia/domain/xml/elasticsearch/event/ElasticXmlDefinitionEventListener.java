package com.example.servicemedia.domain.xml.elasticsearch.event;

import com.example.servicemedia.domain.xml.dto.XmlDefinitionDto;
import com.example.servicemedia.domain.xml.elasticsearch.model.ElasticXmlDefinition;
import com.example.servicemedia.domain.xml.elasticsearch.repo.ElasticXmlDefinitionRepository;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticXmlDefinitionEventListener {
    private final ElasticXmlDefinitionRepository repository;

    @Async
    @EventListener(CreateXmlDefinitionEvent.class)
    public void createContent(CreateXmlDefinitionEvent event) {
        repository.save(toEntity(new ElasticXmlDefinition(),event.xmlDefinition()), RefreshPolicy.IMMEDIATE);
    }

    @Async
    @EventListener(UpdateXmlDefinitionEvent.class)
    public void updateContent(UpdateXmlDefinitionEvent event) {
        ElasticXmlDefinition elasticXmlDefinition = repository.findById(event.xmlDefinition().getId()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticXmlDefinition.class.getSimpleName(), event.xmlDefinition().getId()));
        repository.save(toEntity(elasticXmlDefinition,event.xmlDefinition()), RefreshPolicy.IMMEDIATE);
    }

    @Async
    @EventListener(DeleteXmlDefinitionEvent.class)
    public void deleteContent(DeleteXmlDefinitionEvent event) {
        ElasticXmlDefinition elasticXmlDefinition = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticXmlDefinition.class.getSimpleName(), event.id()));
        repository.delete(elasticXmlDefinition, RefreshPolicy.IMMEDIATE);
    }

    private ElasticXmlDefinition toEntity(ElasticXmlDefinition entity, XmlDefinitionDto dto) {
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated().atOffset(ZoneOffset.UTC));
        entity.setFileName(dto.getFileName());
        entity.setSuccess(dto.getSuccess());
        entity.setJobExecutionId(dto.getJobExecutionId());
        entity.setType(dto.getType().name());

        return entity;
    }
}
