package com.example.servicemedia.media.service;

import com.example.servicemedia.content.service.ContentService;
import com.example.servicemedia.feign.LikeCountResponse;
import com.example.servicemedia.feign.LikeFeignClient;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final MediaSourceRepository mediaSourceRepository;
    private final LikeFeignClient likeFeignClient;
    private final ContentService contentService;

    public MediaServiceImpl(MediaRepository mediaRepository, MediaSourceRepository mediaSourceRepository, LikeFeignClient likeFeignClient,@Lazy ContentService contentService) {
        this.mediaRepository = mediaRepository;
        this.mediaSourceRepository = mediaSourceRepository;
        this.likeFeignClient = likeFeignClient;
        this.contentService = contentService;
    }

    @Override
    public Page<MediaDto> getAll(Pageable pageable) {
        return mediaRepository.findAll(pageable).map(this::toMediaTo);
    }

    @Override
    public Page<MediaDto> getByContentId(Pageable pageable, String contentId) {
        return mediaRepository.findAllByContentId(contentId, pageable).map(this::toMediaTo);
    }

    @Override
    public Page<MediaDto> getNewMedias() {
        Sort sort = Sort.by(Sort.Direction.DESC,"created");
        Pageable pageRequest = PageRequest.of(0,12,sort);
        return mediaRepository.findNewMedias(pageRequest).map(media -> {
            MediaDto mediaDto = toMediaTo(media);
            mediaDto.setContent(contentService.getById(media.getContentId()));
            return mediaDto;
        });
    }

    @Override
    public MediaDto getById(String id) {
        return mediaRepository.findById(id).map(this::toMediaTo).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
    }

    @Override
    public MediaDto getBySlug(String slug) {
        return mediaRepository.findBySlug(slug).map(this::toMediaTo).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), slug));
    }

    @Override
    @Transactional
    public void save(MediaDto mediaDto) {
        Media media = new Media();
        media.setMediaSources(new ArrayList<>());
        if (mediaDto.getMediaSourceList() != null && !mediaDto.getMediaSourceList().isEmpty()) {
            mediaDto.getMediaSourceList().forEach(mediaSourceDto -> media.getMediaSources().add(new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, mediaSourceDto.getFanSub())));
        }
        mediaRepository.save(MediaServiceMapper.toEntity(media, mediaDto));
    }

    @Override
    @Transactional
    public void update(String id, MediaDto mediaDto) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        if (mediaDto.getMediaSourceList() != null && !mediaDto.getMediaSourceList().isEmpty()) {
            mediaSourceRepository.deleteMediaSourcesByMediaId(id);
            media.getMediaSources().clear();
            media.getMediaSources().addAll(mediaDto.getMediaSourceList().stream()
                    .map(mediaSourceDto -> new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, mediaSourceDto.getFanSub()))
                    .toList());
        }
        mediaRepository.save(MediaServiceMapper.toEntity(media, mediaDto));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        mediaRepository.delete(media);
    }

    @Override
    public List<MediaDto> getByContentId(String contentId) {
        return mediaRepository.findAllByContentId(contentId).stream()
                .sorted(Comparator.comparing(Media::getCount).reversed())
                .map(this::toMediaTo)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAllByContentId(String contentId) {
        mediaRepository.deleteAllByContentId(contentId); //TODO KAFKA ILE SAGA AKISI KURULMALIDIR
    }

    @Override
    public List<MediaSourceDto> getMediaSourcesByMediaId(String mediaId) {
        return mediaSourceRepository.findAllByMediaId(mediaId).stream()
                .map(MediaServiceMapper::toMediaSourceDto)
                .toList();
    }

    @Override
    @Transactional
    public void updateMediaSources(String mediaId, MediaSourceRequest request) {
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), mediaId));
        if (request.mediaSources() != null && !request.mediaSources().isEmpty()) {
            mediaSourceRepository.deleteMediaSourcesByMediaId(mediaId);
            media.getMediaSources().clear();
            mediaRepository.flush();
            media.getMediaSources().addAll(request.mediaSources().stream()
                    .map(mediaSourceDto -> new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, mediaSourceDto.getFanSub()))
                    .toList());
        }
    }

    @Override
    @Transactional
    public void increaseNumberOfViews(String id) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getSimpleName(), id));
        media.setNumberOfViews(media.getNumberOfViews()+ 1);
        mediaRepository.save(media);
    }

    private MediaDto toMediaTo(Media media) {
        MediaDto dto = MediaServiceMapper.toDto(media);
        ResponseEntity<LikeCountResponse> likeCountResponseResponseEntity = likeFeignClient.getLikeCount(media.getId(), null);
        dto.setLikeCount(likeCountResponseResponseEntity.getBody());
        return dto;
    }
}
