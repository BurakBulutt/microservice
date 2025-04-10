package com.example.servicemedia.media.service;

import com.example.servicemedia.media.api.MediaSourceRequest;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MediaService {
    Page<MediaDto> getAll(Pageable pageable,String name);
    Page<MediaDto> getByContentId(Pageable pageable, String contentId,String name);
    Page<MediaDto> getNewMedias();
    List<MediaDto> getByContentId(String contentId);
    List<MediaSourceDto> getMediaSourcesByMediaId(String mediaId);
    MediaDto getById(String id);
    MediaDto getBySlug(String slug);

    void save(MediaDto mediaDto);
    void update(String id, MediaDto mediaDto);
    void delete(String id);
    void updateMediaSources(String mediaId, MediaSourceRequest request);
    void increaseNumberOfViews(String id);
}
