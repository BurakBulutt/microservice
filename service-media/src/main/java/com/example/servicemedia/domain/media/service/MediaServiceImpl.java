package com.example.servicemedia.domain.media.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.servicemedia.domain.content.mapper.ContentServiceMapper;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.domain.content.service.ContentService;
import com.example.servicemedia.domain.fansub.mapper.FansubServiceMapper;
import com.example.servicemedia.domain.fansub.model.Fansub;
import com.example.servicemedia.domain.fansub.service.FansubService;
import com.example.servicemedia.domain.media.elasticsearch.model.ElasticMedia;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.feign.like.LikeFeignClient;
import com.example.servicemedia.domain.media.constants.MediaConstants;
import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.dto.MediaSourceDto;
import com.example.servicemedia.domain.media.mapper.MediaServiceMapper;
import com.example.servicemedia.domain.media.model.Media;
import com.example.servicemedia.domain.media.model.MediaSource;
import com.example.servicemedia.domain.media.repo.MediaRepository;
import com.example.servicemedia.domain.media.repo.MediaSourceRepository;
import com.example.servicemedia.util.exception.BaseException;
import com.example.servicemedia.util.exception.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.*;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.servicemedia.util.CreatorComponent.fullTextSearchQuery;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@CacheConfig(cacheNames = MediaConstants.CACHE_NAME_MEDIA)
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final MediaSourceRepository mediaSourceRepository;
    private final LikeFeignClient likeFeignClient;
    private final ContentService contentService;
    private final StreamBridge streamBridge;
    private final CacheManager cacheManager;
    private final FansubService fansubService;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    @Cacheable(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, key = "'media-all:' +#pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()")
    public Page<MediaDto> getAll(Pageable pageable) {
        log.info("Getting all medias");
        return mediaRepository.findAll(pageable).map(this::toMediaDto);
    }

    @Override
    @Cacheable(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, key = "'media-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()", condition = "#content == null and #query == null")
    public Page<MediaDto> filter(Pageable pageable, String content, String query) {
        log.info("Getting filtered medias: [content: {}, query: {}]", content, query);

        BoolQuery.Builder queryBuilder = QueryBuilders.bool();

        if (query != null && query.length() >= 2) {
            queryBuilder.must(fullTextSearchQuery(query));
        }

        if (content != null) {
            queryBuilder.filter(QueryBuilders.term()
                    .field("contentId")
                    .value(content)
                    .build()
                    ._toQuery());
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(queryBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();
        SearchHits<ElasticMedia> search = elasticsearchOperations.search(nativeQuery, ElasticMedia.class);
        Set<String> ids = search.getSearchHits().stream().map(hit -> hit.getContent().getId()).collect(Collectors.toSet());
        return new PageImpl<>(mediaRepository.findAllByIdIn(ids, nativeQuery.getSort()), pageable, search.getTotalHits()).map(this::toMediaDto);
    }

    @Override
    @Cacheable(key = "'media-id:' + #id")
    public MediaDto getById(String id) {
        log.info("Getting media: {}", id);
        return mediaRepository.findById(id).map(this::toMediaDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
    }

    @Override
    @Cacheable(key = "'media-slug:' + #slug")
    public MediaDto getBySlug(String slug) {
        log.info("Getting media with slug: {}", slug);
        return mediaRepository.findBySlug(slug).map(this::toMediaDto).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), slug));
    }

    @Override
    public Long getCount() {
        return mediaRepository.count();
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'media-id:' + #result.id"),
                    @CachePut(key = "'media-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, allEntries = true)
    )
    public MediaDto save(MediaDto mediaDto) {
        Media media = new Media();
        media.setMediaSources(Collections.emptyList());
        Content content = contentService.findById(mediaDto.getContent().getId());
        log.warn("Saving media: {}", mediaDto);
        return toMediaDto(mediaRepository.save(MediaServiceMapper.toEntity(media, content, mediaDto)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, allEntries = true)
    public void saveMediasBulk(List<MediaDto> mediaDtoList) {
        log.info("Medias are Saving: {}", mediaDtoList.toString());
        List<Media> mediaList = new ArrayList<>();

        mediaDtoList.forEach(mediaDto -> {
            Content content = contentService.findById(mediaDto.getContent().getId());
            List<MediaSource> mediaSources = new ArrayList<>();

            Media media = MediaServiceMapper.toEntity(new Media(), content, mediaDto);
            media.setMediaSources(mediaSources);

            mediaDto.getMediaSourceList().forEach(mediaSourceDto -> {
                Fansub fansub = fansubService.findOrCreateByName(mediaSourceDto.getFansub().getName());

                mediaSources.add(MediaServiceMapper.toMediaSourceEntity(new MediaSource(), mediaSourceDto, media, fansub));
            });
            mediaList.add(media);
        });

        mediaRepository.saveAllAndFlush(mediaList);
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'media-id:' + #id"),
                    @CachePut(key = "'media-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, allEntries = true)
    )
    public MediaDto update(String id, MediaDto mediaDto) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        Content content = contentService.findById(mediaDto.getContent().getId());

        log.warn("Updating media: {}, updated: {}", id, mediaDto);
        return toMediaDto(mediaRepository.save(MediaServiceMapper.toEntity(media, content, mediaDto)));
    }

    @Override
    public List<MediaSourceDto> getMediaSourcesByMediaId(String mediaId) {
        log.info("Getting media media sources: {}", mediaId);
        return mediaSourceRepository.findAllByMediaId(mediaId).stream()
                .map(this::toMediaSourceDto)
                .toList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateMediaSources(String mediaId, List<MediaSourceDto> mediaSourceDtoList) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), mediaId));
        List<MediaSource> mediaSourceList = mediaSourceRepository.findAllByMediaId(media.getId());
        log.warn("Media sources clearing: {}", mediaId);
        mediaSourceRepository.deleteAll(mediaSourceList);
        mediaSourceRepository.flush();
        log.warn("Saving new media sources : {}", mediaSourceDtoList.toString());
        mediaSourceRepository.saveAll(mediaSourceDtoList.stream()
                .map(mediaSourceDto -> {
                    Fansub fansub = fansubService.findOrCreateByName(mediaSourceDto.getFansub().getName());
                    return new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, fansub);
                })
                .toList());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, allEntries = true),
            @CacheEvict(key = "'media-id:' + #id")
    })
    public void delete(String id) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));

        log.warn("Deleting media : {}", id);
        mediaRepository.delete(media);

        Cache cache = cacheManager.getCache(MediaConstants.CACHE_NAME_MEDIA);

        if (cache != null) {
            cache.evict("media-slug:" + media.getSlug());
        }

        boolean deleteComments = streamBridge.send("deleteComments-out-0", Set.of(id));
        log.info("Sending delete media comments message: {}, status: {}", id, deleteComments);
    }

    private MediaDto toMediaDto(Media media) {
        MediaDto dto = MediaServiceMapper.toDto(media);
        dto.setContent(ContentServiceMapper.toDto(media.getContent()));
        dto.setMediaSourceList(media.getMediaSources().stream().map(this::toMediaSourceDto).toList());
        dto.setLikeCount(getLikeCount(media.getId()));

        return dto;
    }

    private LikeCountResponse getLikeCount(String id) {
        ResponseEntity<LikeCountResponse> response = likeFeignClient.getLikeCount(id);
        return response.getBody();
    }

    private MediaSourceDto toMediaSourceDto(MediaSource mediaSource) {
        MediaSourceDto dto = MediaServiceMapper.toMediaSourceDto(mediaSource);
        dto.setMedia(MediaServiceMapper.toDto(mediaSource.getMedia()));
        dto.setFansub(FansubServiceMapper.toDto(mediaSource.getFansub()));
        return dto;
    }
}
