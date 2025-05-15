package com.example.servicemedia.media.service;

import com.example.servicemedia.media.api.MediaSourceRequest;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MediaService {
    Page<MediaDto> getAll(Pageable pageable);
    Page<MediaDto> filter(Pageable pageable,String contentId,String name);

    List<MediaSourceDto> getMediaSourcesByMediaId(String mediaId);

    MediaDto getById(String id);
    MediaDto getBySlug(String slug);

    Long getCount();

    MediaDto save(MediaDto mediaDto);
    void saveMediasBulk(List<MediaDto> mediaDtoList);
    MediaDto update(String id, MediaDto mediaDto);
    void delete(String id);
    List<MediaSourceDto> updateMediaSources(String mediaId, MediaSourceRequest request);
}
