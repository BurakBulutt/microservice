package com.example.servicemedia.domain.category.elasticsearch.event.listener;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.category.elasticsearch.event.CreateCategoryEvent;
import com.example.servicemedia.domain.category.elasticsearch.event.DeleteCategoryEvent;
import com.example.servicemedia.domain.category.elasticsearch.event.UpdateCategoryEvent;
import com.example.servicemedia.domain.category.elasticsearch.model.ElasticCategory;
import com.example.servicemedia.domain.category.elasticsearch.repo.ElasticCategoryRepository;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ElasticCategoryEventListener {
    private final ElasticCategoryRepository repository;

    @EventListener(CreateCategoryEvent.class)
    public void createContent(CreateCategoryEvent event) {
        repository.save(toEntity(new ElasticCategory(),event.category()), RefreshPolicy.IMMEDIATE);
    }

    @EventListener(UpdateCategoryEvent.class)
    public void updateContent(UpdateCategoryEvent event) {
        ElasticCategory elasticCategory = repository.findById(event.category().getId()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticCategory.class.getSimpleName(), event.category().getId()));
        repository.save(toEntity(elasticCategory,event.category()), RefreshPolicy.IMMEDIATE);
    }

    @EventListener(DeleteCategoryEvent.class)
    public void deleteContent(DeleteCategoryEvent event) {
        ElasticCategory elasticCategory = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticCategory.class.getSimpleName(), event.id()));
        repository.delete(elasticCategory, RefreshPolicy.IMMEDIATE);
    }

    private ElasticCategory toEntity(ElasticCategory entity, CategoryDto dto) {
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated().atOffset(ZoneOffset.UTC));
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());

        return entity;
    }

}
