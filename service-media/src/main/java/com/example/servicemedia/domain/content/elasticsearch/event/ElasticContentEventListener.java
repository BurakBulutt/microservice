package com.example.servicemedia.domain.content.elasticsearch.event;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.domain.content.elasticsearch.model.ElasticContent;
import com.example.servicemedia.domain.content.elasticsearch.repo.ElasticContentRepository;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ElasticContentEventListener {
    private final ElasticContentRepository repository;

    @Async
    @EventListener(CreateContentEvent.class)
    public void createContent(CreateContentEvent event) {
        repository.save(toEntity(new ElasticContent(),event.content()), RefreshPolicy.IMMEDIATE);
    }

    @Async
    @EventListener(UpdateContentEvent.class)
    public void updateContent(UpdateContentEvent event) {
        ElasticContent elasticContent = repository.findById(event.content().getId()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticContent.class.getSimpleName(), event.content().getId()));
        repository.save(toEntity(elasticContent,event.content()), RefreshPolicy.IMMEDIATE);
    }

    @Async
    @EventListener(DeleteContentEvent.class)
    public void deleteContent(DeleteContentEvent event) {
        ElasticContent elasticContent = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticContent.class.getSimpleName(), event.id()));
        repository.delete(elasticContent, RefreshPolicy.IMMEDIATE);
    }

    private ElasticContent toEntity(ElasticContent entity,ContentDto dto) {
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated().atOffset(ZoneOffset.UTC));
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setPhotoUrl(dto.getPhotoUrl());
        entity.setCategories(dto.getCategories().stream().map(CategoryDto::getId).toList());
        entity.setType(dto.getType().name());
        entity.setStartDate(dto.getStartDate());

        return entity;
    }

}
