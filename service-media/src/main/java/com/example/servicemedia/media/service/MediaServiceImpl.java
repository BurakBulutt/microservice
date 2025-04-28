package com.example.servicemedia.media.service;

import com.example.servicemedia.content.mapper.ContentServiceMapper;
import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.content.service.ContentService;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.feign.like.LikeFeignClient;
import com.example.servicemedia.media.api.MediaSourceRequest;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import com.example.servicemedia.media.mapper.MediaServiceMapper;
import com.example.servicemedia.media.model.Media;
import com.example.servicemedia.media.model.MediaSource;
import com.example.servicemedia.media.repo.MediaRepository;
import com.example.servicemedia.media.repo.MediaSourceRepository;
import com.example.servicemedia.media.repo.MediaSpec;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
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
@CacheConfig(cacheNames = "mediaCache")
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final MediaSourceRepository mediaSourceRepository;
    private final LikeFeignClient likeFeignClient;
    private final ContentService contentService;
    private final StreamBridge streamBridge;
    private final CacheManager cacheManager;


    @Override
    @Cacheable(value = "mediaPageCache" ,key = "'media-all:' +#pageable.getPageNumber() + '_' + #pageable.getPageSize()")
    public Page<MediaDto> getAll(Pageable pageable) {
        log.info("Getting all medias");
        return mediaRepository.findAll(pageable).map(this::toMediaTo);
    }

    @Override
    @Cacheable(value = "mediaPageCache" ,key = "'media-filter:' + #pageable.getPageNumber() + '_' + #pageable.getPageSize()",condition = "#contentId == null and #name == null")
    public Page<MediaDto> filter(Pageable pageable, String contentId,String name) {
        Specification<Media> specification = Specification.where(MediaSpec.byContentId(contentId)).and(MediaSpec.nameContainsIgnoreCase(name));
        log.info("Getting filtered medias");
        return mediaRepository.findAll(specification,pageable).map(this::toMediaTo);
    }

    @Override
    @Cacheable(key = "'media-id:' + #id")
    public MediaDto getById(String id) {
        log.info("Getting media: {}", id);
        return mediaRepository.findById(id).map(this::toMediaTo).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
    }

    @Override
    @Cacheable(key = "'media-slug:' + #slug")
    public MediaDto getBySlug(String slug) {
        log.info("Getting media with slug: {}", slug);
        return mediaRepository.findBySlug(slug).map(this::toMediaTo).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), slug));
    }

    @Override
    @Cacheable(value = "mediaSourceListCache" ,key = "'media-source:' + #mediaId")
    public List<MediaSourceDto> getMediaSourcesByMediaId(String mediaId) {
        log.info("Getting media media sources: {}", mediaId);
        return mediaSourceRepository.findAllByMediaId(mediaId).stream()
                .map(MediaServiceMapper::toMediaSourceDto)
                .toList();
    }

    @Override
    @Transactional
    public MediaDto save(MediaDto mediaDto) {
        Media media = new Media();
        media.setMediaSources(Collections.emptyList());
        Content content = contentService.findById(mediaDto.getContent().getId());
        mediaDto.setName(content.getName() + " " + mediaDto.getCount() + ". Bölüm");
        mediaDto.setSlug(slugGenerator(mediaDto.getName()));
        mediaDto.setNumberOfViews(0);
        log.warn("Saving media: {}", mediaDto);
        return MediaServiceMapper.toDto(mediaRepository.save(MediaServiceMapper.toEntity(media, content, mediaDto)));
    }

    @Override
    @Transactional
    public void saveMediasBulk(List<MediaDto> mediaDtoList) {
        log.info("Medias are Saving: {}",mediaDtoList.toString());
        List<Media> mediaList = new ArrayList<>();

        mediaDtoList.forEach(mediaDto -> {
            Content content = contentService.findById(mediaDto.getContent().getId());

            Media media = new Media();
            media.setDescription(mediaDto.getDescription());
            media.setCount(mediaDto.getCount());
            media.setPublishDate(mediaDto.getPublishDate());
            media.setNumberOfViews(0);
            media.setContent(content);
            media.setName(content.getName() + " " + media.getCount() + ". Bölüm");
            media.setSlug(slugGenerator(media.getName()));

            List<MediaSource> mediaSources = new ArrayList<>();
            media.setMediaSources(mediaSources);

            List<MediaSourceDto> mediaSourceDtoList = mediaDto.getMediaSourceList();
            mediaSourceDtoList.forEach(mediaSourceDto ->  {
                MediaSource mediaSource = new MediaSource();
                mediaSource.setMedia(media);
                mediaSource.setUrl(mediaSourceDto.getUrl());
                mediaSource.setFanSub(mediaSourceDto.getFanSub());
                mediaSource.setType(mediaSourceDto.getType());

                mediaSources.add(mediaSource);
            });
            mediaList.add(media);
        });

        try {
            List<Media> chunkList = new ArrayList<>();

            for (int i = 0; i < mediaList.size(); i++) {
                chunkList.add(mediaList.get(i));
                if (i % 10 == 9) {
                    mediaRepository.saveAllAndFlush(chunkList);
                    chunkList.clear();
                }
            }

            if (!chunkList.isEmpty()) {
                mediaRepository.saveAllAndFlush(chunkList);
            }
        } catch (Exception e) {
            log.error("Media bulk save failing : {}",e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(key = "'media-id:' + #id"),
                    @CachePut(key = "'media-slug:' + #result.slug")
            },
            evict = @CacheEvict(value = {"mediaPageCache,","mediaSourceListCache"}, allEntries = true)
    )
    public MediaDto update(String id, MediaDto mediaDto) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        Content content = contentService.findById(mediaDto.getContent().getId());
        mediaDto.setName(content.getName() + " " + mediaDto.getCount() + ". Bölüm");
        mediaDto.setSlug(slugGenerator(mediaDto.getName()));
        log.warn("Updating media: {}, updated: {}", id, mediaDto);
        return MediaServiceMapper.toDto(mediaRepository.save(MediaServiceMapper.toEntity(media, content, mediaDto)));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CachePut(value = "mediaSourceListCache" ,key = "'media-source:' + #mediaId")
    public List<MediaSourceDto> updateMediaSources(String mediaId, MediaSourceRequest request) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), mediaId));
        log.warn("Media sources clearing: {}", mediaId);
        media.getMediaSources().clear();
        mediaRepository.flush();
        log.warn("Saving new media sources : {}", request.mediaSources().toString());
        media.getMediaSources().addAll(request.mediaSources().stream()
                .map(mediaSourceDto -> new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, mediaSourceDto.getFanSub()))
                .toList());
        return media.getMediaSources().stream().map(MediaServiceMapper::toMediaSourceDto).toList();
    }

    @Override
    @Transactional
    public void increaseNumberOfViews(String id) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        media.setNumberOfViews(media.getNumberOfViews() + 1);
        log.warn("Media views increased: {}, view count: {}", id, media.getNumberOfViews());
        mediaRepository.save(media);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"mediaPageCache","mediaSourceListCache"}, allEntries = true),
            @CacheEvict(key = "'media-id:' + #id")
    })
    public void delete(String id) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        mediaRepository.delete(media);
        log.warn("Media is deleted: {}", id);

        Cache cache = cacheManager.getCache("mediaCache");

        if (cache != null) {
            cache.evict("media-slug:" + media.getSlug());
        }

        boolean deleteComments = streamBridge.send("deleteComments-out-0", Set.of(id));
        log.info("Sending delete media comments message: {}, status: {}", id, deleteComments);
    }

    private MediaDto toMediaTo(Media media) {
        MediaDto dto = MediaServiceMapper.toDto(media);
        dto.setContent(ContentServiceMapper.toDto(media.getContent()));
        dto.setMediaSourceList(media.getMediaSources().stream().map(MediaServiceMapper::toMediaSourceDto).toList());
        ResponseEntity<LikeCountResponse> response = likeFeignClient.getLikeCount(media.getId());
        if (response.getBody() != null) {
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
}
