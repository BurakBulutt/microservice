package com.example.servicemedia.domain.fansub.elasticsearch.event;

import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.fansub.elasticsearch.model.ElasticFansub;
import com.example.servicemedia.domain.fansub.elasticsearch.repo.ElasticFansubRepository;
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
public class ElasticFansubEventListener {
    private final ElasticFansubRepository repository;

    @Async
    @EventListener(CreateFansubEvent.class)
    public void createContent(CreateFansubEvent event) {
        repository.save(toEntity(new ElasticFansub(),event.fansub()), RefreshPolicy.IMMEDIATE);
    }

    @Async
    @EventListener(UpdateFansubEvent.class)
    public void updateContent(UpdateFansubEvent event) {
        ElasticFansub elasticFansub = repository.findById(event.fansub().getId()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticFansub.class.getSimpleName(), event.fansub().getId()));
        repository.save(toEntity(elasticFansub,event.fansub()), RefreshPolicy.IMMEDIATE);
    }

    @Async
    @EventListener(DeleteFansubEvent.class)
    public void deleteContent(DeleteFansubEvent event) {
        ElasticFansub elasticFansub = repository.findById(event.id()).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, ElasticFansub.class.getSimpleName(), event.id()));
        repository.delete(elasticFansub, RefreshPolicy.IMMEDIATE);
    }

    private ElasticFansub toEntity(ElasticFansub entity, FansubDto dto) {
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated().atOffset(ZoneOffset.UTC));
        entity.setName(dto.getName());

        return entity;
    }

}
