package com.example.servicemedia.domain.fansub.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.servicemedia.domain.content.constants.ContentConstants;
import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.fansub.elasticsearch.event.CreateFansubEvent;
import com.example.servicemedia.domain.fansub.elasticsearch.event.DeleteFansubEvent;
import com.example.servicemedia.domain.fansub.elasticsearch.event.UpdateFansubEvent;
import com.example.servicemedia.domain.fansub.elasticsearch.model.ElasticFansub;
import com.example.servicemedia.domain.fansub.mapper.FansubServiceMapper;
import com.example.servicemedia.domain.fansub.model.Fansub;
import com.example.servicemedia.domain.fansub.repo.FansubRepository;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
@RequiredArgsConstructor
public class FansubServiceImpl implements FansubService {
    private final FansubRepository repository;
    private final ApplicationEventPublisher publisher;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<FansubDto> getAll(Pageable pageable) {
        log.info("Getting all fansubs");
        return repository.findAll(pageable).map(FansubServiceMapper::toDto);
    }

    @Override
    public Page<FansubDto> filter(Pageable pageable,String query) {
        log.info("Getting filtered fansubs, [query: {}]",query);

        BoolQuery.Builder queryBuilder = QueryBuilders.bool();

        if (query != null && query.length() >= 2) {
            queryBuilder.must(fullTextSearchQuery(query));
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();
        SearchHits<ElasticFansub> search = elasticsearchOperations.search(nativeQuery, ElasticFansub.class);
        Set<String> ids = search.getSearchHits().stream().map(hit -> hit.getContent().getId()).collect(Collectors.toSet());
        return repository.findAllByIdIn(ids,pageable).map(FansubServiceMapper::toDto);
    }

    @Override
    public Long count() {
        log.info("Getting fansubs count");
        return repository.count();
    }

    @Override
    public FansubDto getById(String id) {
        log.info("Getting fansub by id: {}", id);
        return repository.findById(id).map(FansubServiceMapper::toDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Fansub.class.getSimpleName(), id));
    }

    @Override
    public Fansub findByName(String name) {
        log.info("Getting fansub by name: {}", name);
        return repository.findByName(name).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Fansub.class.getSimpleName(), name));
    }

    @Override
    @Transactional
    public Fansub findOrCreateByName(String name) {
        log.info("Getting fansub by name: {}, if not found, it will be created with name", name);
        Optional<Fansub> fansub = repository.findByName(name);
        return fansub.orElseGet(() -> repository.save(new Fansub(name, null, Collections.emptyList())));
    }

    @Override
    @Transactional
    public FansubDto save(FansubDto fanSubDto) {
        log.info("Saving fansub: {}", fanSubDto);

        FansubDto dto = FansubServiceMapper.toDto(repository.save(FansubServiceMapper.toEntity(new Fansub(),fanSubDto)));
        publisher.publishEvent(new CreateFansubEvent(dto));
        return dto;
    }

    @Override
    @Transactional
    public FansubDto update(String id, FansubDto fanSubDto) {
        Fansub fanSub = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Fansub.class.getSimpleName(), id));

        log.info("Updating fansub: {}, updated: {}",id,fanSubDto);
        FansubDto dto = FansubServiceMapper.toDto(repository.save(FansubServiceMapper.toEntity(fanSub,fanSubDto)));
        publisher.publishEvent(new UpdateFansubEvent(dto));
        return dto;
    }

    @Override
    @Transactional
    public void delete(String id) {
        Fansub fanSub = repository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Fansub.class.getSimpleName(), id));

        log.warn("Deleting fansub: {}, updated: {}",id,fanSub);
        repository.delete(fanSub);
        publisher.publishEvent(new DeleteFansubEvent(id));
    }

    private Query fullTextSearchQuery(String query) {
        return QueryBuilders.match()
                .field(ContentConstants.SEARCH_FIELD_NAME)
                .query(query)
                .fuzziness(ContentConstants.SEARCH_FUZZINESS)
                .build()
                ._toQuery();
    }
}
