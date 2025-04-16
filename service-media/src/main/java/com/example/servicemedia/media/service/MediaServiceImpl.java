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
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final MediaSourceRepository mediaSourceRepository;
    private final LikeFeignClient likeFeignClient;
    private final ContentService contentService;
    private final StreamBridge streamBridge;


    @Override
    public Page<MediaDto> getAll(Pageable pageable,String name) {
        log.info("Getting all medias");
        if (StringUtils.hasLength(name)){
            return mediaRepository.findAllByNameContainsIgnoreCase(name,pageable).map(this::toMediaTo);
        }
        return mediaRepository.findAll(pageable).map(this::toMediaTo);
    }

    @Override
    public Page<MediaDto> getByContentId(Pageable pageable, String contentId,String name) {
        log.info("Getting content medias: {}", contentId);
        if (StringUtils.hasLength(name)){
            return mediaRepository.findAllByContentIdAndNameContainsIgnoreCase(contentId,name,pageable).map(this::toMediaTo);
        }
        return mediaRepository.findAllByContentId(contentId, pageable).map(this::toMediaTo);
    }

    @Override
    public Page<MediaDto> getNewMedias() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable pageRequest = PageRequest.of(0, 12, sort);
        log.info("Getting new medias");
        return mediaRepository.findNewMedias(pageRequest).map(this::toMediaTo);
    }

    @Override
    public MediaDto getById(String id) {
        log.info("Getting media: {}", id);
        return mediaRepository.findById(id).map(this::toMediaTo).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
    }

    @Override
    public MediaDto getBySlug(String slug) {
        log.info("Getting media with slug: {}", slug);
        return mediaRepository.findBySlug(slug).map(this::toMediaTo).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), slug));
    }

    @Override
    public List<MediaDto> getByContentId(String contentId) {
        log.info("Getting content media list: {}", contentId);
        return mediaRepository.findAllByContentId(contentId).stream()
                .sorted(Comparator.comparing(Media::getCount).reversed())
                .map(this::toMediaTo)
                .toList();
    }

    @Override
    public List<MediaSourceDto> getMediaSourcesByMediaId(String mediaId) {
        log.info("Getting media media sources: {}", mediaId);
        return mediaSourceRepository.findAllByMediaId(mediaId).stream()
                .map(MediaServiceMapper::toMediaSourceDto)
                .toList();
    }

    @Override
    @Transactional
    public void save(MediaDto mediaDto) {
        Media media = new Media();
        media.setMediaSources(Collections.emptyList());
        Content content = contentService.findById(mediaDto.getContent().getId());
        mediaDto.setName(content.getName() + " " + mediaDto.getCount() + ". Bölüm");
        mediaDto.setSlug(slugGenerator(mediaDto.getName()));
        mediaDto.setNumberOfViews(0);
        log.warn("Saving media: {}", mediaDto);
        mediaRepository.save(MediaServiceMapper.toEntity(media, content, mediaDto));
    }

    @Override
    @Transactional
    public void update(String id, MediaDto mediaDto) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        Content content = contentService.findById(mediaDto.getContent().getId());
        mediaDto.setName(content.getName() + " " + mediaDto.getCount() + ". Bölüm");
        mediaDto.setSlug(slugGenerator(mediaDto.getName()));
        log.warn("Updating media: {}, updated: {}", id, mediaDto);
        mediaRepository.save(MediaServiceMapper.toEntity(media, content, mediaDto));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateMediaSources(String mediaId, MediaSourceRequest request) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), mediaId));
        log.warn("Media sources clearing: {}", mediaId);
        media.getMediaSources().clear();
        mediaRepository.flush();
        log.warn("Saving new media sources : {}", request.mediaSources().toString());
        media.getMediaSources().addAll(request.mediaSources().stream()
                .map(mediaSourceDto -> new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, mediaSourceDto.getFanSub()))
                .toList());
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
    public void delete(String id) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        mediaRepository.delete(media);
        log.warn("Media is deleted: {}", id);

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
