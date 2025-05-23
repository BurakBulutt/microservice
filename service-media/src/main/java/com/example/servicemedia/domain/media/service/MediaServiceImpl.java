package com.example.servicemedia.domain.media.service;

import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.domain.content.mapper.ContentServiceMapper;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.domain.content.service.ContentService;
import com.example.servicemedia.domain.fansub.model.Fansub;
import com.example.servicemedia.domain.fansub.service.FansubService;
import com.example.servicemedia.domain.media.repo.MediaSpec;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Override
    @Cacheable(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, key = "'media-all:' +#pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()")
    public Page<MediaDto> getAll(Pageable pageable) {
        log.info("Getting all medias");
        return mediaRepository.findAll(pageable).map(this::toMediaDto);
    }

    @Override
    @Cacheable(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, key = "'media-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize() + '_' + #pageable.getSort().toString()",condition = "#content == null")
    public Page<MediaDto> filter(Pageable pageable, String content) {
        Specification<Media> spec = Specification.where(MediaSpec.byContentId(content));
        log.info("Getting filtered medias: [content: {}]", content);
        return mediaRepository.findAll(spec,pageable).map(this::toMediaDto);
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
    @CacheEvict(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, allEntries = true)
    public MediaDto save(MediaDto mediaDto) {
        Media media = new Media();
        media.setMediaSources(Collections.emptyList());
        Content content = contentService.findById(mediaDto.getContent().getId());
        mediaDto.setName(nameGenerator(content.getName(), mediaDto.getCount(), content.getType()));
        mediaDto.setSlug(slugGenerator(mediaDto.getName()));
        log.warn("Saving media: {}", mediaDto);
        return MediaServiceMapper.toDto(mediaRepository.save(MediaServiceMapper.toEntity(media, content, mediaDto)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value = MediaConstants.CACHE_NAME_MEDIA_PAGE, allEntries = true)
    public void saveMediasBulk(List<MediaDto> mediaDtoList) {
        log.info("Medias are Saving: {}", mediaDtoList.toString());
        List<Media> mediaList = new ArrayList<>();

        mediaDtoList.forEach(mediaDto -> {
            Content content = contentService.findById(mediaDto.getContent().getId());

            Media media = new Media();

            media.setDescription(mediaDto.getDescription());
            media.setCount(mediaDto.getCount());
            media.setPublishDate(mediaDto.getPublishDate());
            media.setContent(content);
            media.setName(nameGenerator(content.getName(), media.getCount(), content.getType()));
            media.setSlug(slugGenerator(media.getName()));

            List<MediaSource> mediaSources = new ArrayList<>();
            media.setMediaSources(mediaSources);

            List<MediaSourceDto> mediaSourceDtoList = mediaDto.getMediaSourceList();
            mediaSourceDtoList.forEach(mediaSourceDto -> {
                MediaSource mediaSource = new MediaSource();
                Fansub fansub = fansubService.findOrCreateByName(mediaSourceDto.getFansub().getName());

                mediaSource.setMedia(media);
                mediaSource.setUrl(mediaSourceDto.getUrl());
                mediaSource.setFansub(fansub);
                mediaSource.setType(mediaSourceDto.getType());

                mediaSources.add(mediaSource);
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
        mediaDto.setName(nameGenerator(content.getName(), media.getCount(), content.getType()));
        mediaDto.setSlug(slugGenerator(mediaDto.getName()));

        log.warn("Updating media: {}, updated: {}", id, mediaDto);
        return toMediaDto(mediaRepository.save(MediaServiceMapper.toEntity(media, content, mediaDto)));
    }

    @Override
    public List<MediaSourceDto> getMediaSourcesByMediaId(String mediaId) {
        log.info("Getting media media sources: {}", mediaId);
        return mediaSourceRepository.findAllByMediaId(mediaId).stream()
                .map(MediaServiceMapper::toMediaSourceDto)
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
                    Fansub fansub = fansubService.findByName(mediaSourceDto.getFansub().getName());
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
        dto.setMediaSourceList(media.getMediaSources().stream().map(MediaServiceMapper::toMediaSourceDto).toList());
        ResponseEntity<LikeCountResponse> response = likeFeignClient.getLikeCount(media.getId());
        if (response.hasBody()) {
            dto.setLikeCount(response.getBody());
        }
        return dto;
    }

    private String slugGenerator(String name) {
        return name
                .toLowerCase()
                .trim()
                .replaceAll("[^\\w\\s-]", "")
                .replaceAll("[\\s_-]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    private String nameGenerator(String contentName, int count, ContentType type) {
        final String name;
        switch (type) {
            case MOVIE -> name = contentName;
            case SERIES -> name = contentName + " " + count + ". Bölüm";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        return name;
    }
}
