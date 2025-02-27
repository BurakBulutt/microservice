package com.example.servicemedia.media.service;

import com.example.servicemedia.media.dto.MediaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MediaService {
    Page<MediaDto> getAll(Pageable pageable);
    Page<MediaDto> getNewMedias(Pageable pageable);
    MediaDto getById(String id);

    MediaDto getBySlug(String slug);

    MediaDto save(MediaDto mediaDto);
    MediaDto update(String id,MediaDto mediaDto);
    void delete(String id);

    List<MediaDto> getByContentId(String contentId);

    @Transactional
    void deleteAllByContentId(String contentId);
}
