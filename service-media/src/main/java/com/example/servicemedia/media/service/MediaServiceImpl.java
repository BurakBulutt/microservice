package com.example.servicemedia.media.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;
    private final MediaSourceRepository mediaSourceRepository;
    private final LikeFeignClient likeFeignClient;

    @Override
    public Page<MediaDto> getAll(Pageable pageable) {
        return mediaRepository.findAll(pageable).map(this::toMediaTo);
    }

    @Override
    public Page<MediaDto> getByContentId(Pageable pageable, String contentId) {
        return mediaRepository.findAllByContentId(contentId, pageable).map(this::toMediaTo);
    }

    @Override
    public Page<MediaDto> getNewMedias(Pageable pageable) {
        return mediaRepository.findNewMedias(pageable).map(this::toMediaTo);
    }

    @Override
    public MediaDto getById(String id) {
        return mediaRepository.findById(id).map(this::toMediaTo).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getName(), id));
    }

    @Override
    public MediaDto getBySlug(String slug) {
        return mediaRepository.findBySlug(slug).map(this::toMediaTo).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getName(), slug));
    }

    @Override
    @Transactional
    public MediaDto save(MediaDto mediaDto) {
        Media media = new Media();
        media.setMediaSources(new ArrayList<>());
        if (mediaDto.getMediaSourceList() != null && !mediaDto.getMediaSourceList().isEmpty()) {
            mediaDto.getMediaSourceList().forEach(mediaSourceDto -> media.getMediaSources().add(new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, mediaSourceDto.getFanSub())));
        }
        return toMediaTo(mediaRepository.save(MediaServiceMapper.toEntity(media, mediaDto)));
    }

    @Override
    @Transactional
    public MediaDto update(String id, MediaDto mediaDto) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getName(), id));
        if (mediaDto.getMediaSourceList() != null && !mediaDto.getMediaSourceList().isEmpty()) {
            mediaSourceRepository.deleteMediaSourcesByMediaId(id);
            media.getMediaSources().clear();
            media.getMediaSources().addAll(mediaDto.getMediaSourceList().stream()
                    .map(mediaSourceDto -> new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, mediaSourceDto.getFanSub()))
                    .toList());
        }
        return toMediaTo(mediaRepository.save(MediaServiceMapper.toEntity(media, mediaDto)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Media media = mediaRepository.findById(id).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getName(), id));
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
        Media media = mediaRepository.findById(mediaId).orElseThrow(() -> new BaseException(MessageResource.NOT_FOUND, Media.class.getName(), mediaId));
        if (request.mediaSources() != null && !request.mediaSources().isEmpty()) {
            mediaSourceRepository.deleteMediaSourcesByMediaId(mediaId);
            media.getMediaSources().clear();
            mediaRepository.flush();
            media.getMediaSources().addAll(request.mediaSources().stream()
                    .map(mediaSourceDto -> new MediaSource(mediaSourceDto.getUrl(), mediaSourceDto.getType(), media, mediaSourceDto.getFanSub()))
                    .toList());
        }
    }

    private MediaDto toMediaTo(Media media) {
        MediaDto dto = MediaServiceMapper.toDto(media);
        ResponseEntity<LikeCountResponse> likeCountResponseResponseEntity = likeFeignClient.getLikeCount(media.getId(), null);
        dto.setLikeCount(likeCountResponseResponseEntity.getBody());
        return dto;
    }
}
