package com.example.servicemedia.media.service;

import com.example.servicemedia.media.api.MediaSourceRequest;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MediaService {
    Page<MediaDto> getAll(Pageable pageable);

    Page<MediaDto> getByContentId(Pageable pageable, String contentId);

    Page<MediaDto> getNewMedias();
    MediaDto getById(String id);

    MediaDto getBySlug(String slug);

    void save(MediaDto mediaDto);
    void update(String id, MediaDto mediaDto);
    void delete(String id);

    List<MediaDto> getByContentId(String contentId);

    @Transactional
    void deleteAllByContentId(String contentId);

    List<MediaSourceDto> getMediaSourcesByMediaId(String mediaId);

    void updateMediaSources(String mediaId, MediaSourceRequest request);

    @Transactional
    void increaseNumberOfViews(String id);
}
