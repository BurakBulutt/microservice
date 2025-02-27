package com.example.servicemedia.media.service;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.service.ContentService;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.mapper.MediaServiceMapper;
import com.example.servicemedia.media.model.Media;
import com.example.servicemedia.media.model.MediaSource;
import com.example.servicemedia.media.repo.MediaRepository;
import com.example.servicemedia.media.repo.MediaSourceRepository;
import com.example.servicemedia.util.rest.BaseException;
import com.example.servicemedia.util.rest.MessageResource;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MediaServiceImpl implements MediaService {
    private static final String NAME_PREFIX = "%s %s. Bölüm";

    private final MediaRepository mediaRepository;
    private final MediaSourceRepository mediaSourceRepository;
    private final ContentService contentService;

    public MediaServiceImpl(MediaRepository mediaRepository,MediaSourceRepository mediaSourceRepository, @Lazy ContentService contentService) {
        this.mediaRepository = mediaRepository;
        this.contentService = contentService;
        this.mediaSourceRepository = mediaSourceRepository;
    }

    @Override
    public Page<MediaDto> getAll(Pageable pageable) {
        return mediaRepository.findAll(pageable).map(this::toMediaTo);
    }

    @Override
    public Page<MediaDto> getNewMedias(Pageable pageable) {
        return mediaRepository.findNewMedias(pageable).map(this::toMediaTo);
    }

    @Override
    public MediaDto getById(String id) {
        return mediaRepository.findById(id).map(MediaServiceMapper::toDto).orElseThrow(()-> new BaseException(MessageResource.NOT_FOUND,Media.class.getName(),id));
    }

    @Override
    public MediaDto getBySlug(String slug) {
        return mediaRepository.findBySlug(slug).map(media -> {
            MediaDto dto = toMediaTo(media);
            dto.setMediaSourceList(media.getMediaSources().stream().map(MediaServiceMapper::toMediaSourceDto).toList());
            return dto;
        }).orElseThrow(()-> new BaseException(MessageResource.NOT_FOUND,Media.class.getName(),slug));
    }

    @Override
    @Transactional
    public MediaDto save(MediaDto mediaDto) {
        Media media = new Media();
        media.setMediaSources(new ArrayList<>());
        if (mediaDto.getMediaSourceList() != null && !mediaDto.getMediaSourceList().isEmpty()) {
            mediaDto.getMediaSourceList().forEach(mediaSourceDto -> media.getMediaSources().add(new MediaSource(mediaSourceDto.getUrl(),mediaSourceDto.getType(),media)));
        }
        ContentDto content = contentService.getById(mediaDto.getContent().getId());
        mediaDto.setName(String.format(NAME_PREFIX,content.getName(),mediaDto.getCount()));
        return toMediaTo(mediaRepository.save(MediaServiceMapper.toEntity(media,mediaDto)));
    }

    @Override
    @Transactional
    public MediaDto update(String id, MediaDto mediaDto) {
        Media media =mediaRepository.findById(id).orElseThrow(()-> new BaseException(MessageResource.NOT_FOUND,Media.class.getName(),id));
        mediaSourceRepository.deleteMediaSourcesByMediaId(id);
        media.getMediaSources().clear();
        if (mediaDto.getMediaSourceList() != null) {
            media.getMediaSources().addAll(mediaDto.getMediaSourceList().stream()
                    .map(mediaSourceDto -> new MediaSource(mediaSourceDto.getUrl(),mediaSourceDto.getType(),media))
                    .toList());
            /*List<MediaSource> newSource = mediaDto.getMediaSourceList().stream()
                    .map(mediaSourceDto -> new MediaSource(mediaSourceDto.getUrl(),mediaSourceDto.getType(),media))
                    .toList();

             */
        }
        ContentDto content = contentService.getById(mediaDto.getContent().getId());
        mediaDto.setName(String.format(NAME_PREFIX,content.getName(),mediaDto.getCount()));
        return toMediaTo(mediaRepository.save(MediaServiceMapper.toEntity(media,mediaDto)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        Media media =mediaRepository.findById(id).orElseThrow(()-> new BaseException(MessageResource.NOT_FOUND,Media.class.getName(),id));
        mediaRepository.delete(media);
    }

    private MediaDto toMediaTo(Media media) {
        ContentDto contentDto = contentService.getById(media.getContentId());
        MediaDto mediaDto = MediaServiceMapper.toDto(media);
        mediaDto.setContent(contentDto);
        return mediaDto;
    }

    @Override
    public List<MediaDto> getByContentId(String contentId) {
        return mediaRepository.findAllByContentId(contentId).stream()
                .map(MediaServiceMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteAllByContentId(String contentId) {
        mediaRepository.deleteAllByContentId(contentId);
    }
}
