package com.example.servicemedia.domain.media.elasticsearch.event.listener;

import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.elasticsearch.event.CreateMediaEvent;
import com.example.servicemedia.domain.media.elasticsearch.event.DeleteMediaEvent;
import com.example.servicemedia.domain.media.elasticsearch.event.UpdateMediaEvent;
import com.example.servicemedia.domain.media.elasticsearch.model.ElasticMedia;
import com.example.servicemedia.domain.media.elasticsearch.repo.ElasticMediaRepository;
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
public class ElasticMediaEventListener {
    private final ElasticMediaRepository repository;

    @EventListener(CreateMediaEvent.class)
    public void createContent(CreateMediaEvent event) {
        repository.save(toEntity(new ElasticMedia(),event.media()), RefreshPolicy.IMMEDIATE);
    }

    @EventListener(UpdateMediaEvent.class)
    public void updateContent(UpdateMediaEvent event) {
        ElasticMedia elasticMedia = repository.findById(event.media().getId()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticMedia.class.getSimpleName(), event.media().getId()));
        repository.save(toEntity(elasticMedia,event.media()), RefreshPolicy.IMMEDIATE);
    }

    @EventListener(DeleteMediaEvent.class)
    public void deleteContent(DeleteMediaEvent event) {
        ElasticMedia elasticMedia = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticMedia.class.getSimpleName(), event.id()));
        repository.delete(elasticMedia, RefreshPolicy.IMMEDIATE);
    }

    private ElasticMedia toEntity(ElasticMedia entity, MediaDto dto) {
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated().atOffset(ZoneOffset.UTC));
        entity.setName(dto.getName());
        entity.setCount(dto.getCount());
        entity.setPublishDate(dto.getPublishDate());
        entity.setSlug(dto.getSlug());
        entity.setContentId(dto.getContent().getId());

        return entity;
    }

}
