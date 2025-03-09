package com.example.servicemedia.content.service;

import com.example.servicemedia.content.dto.ContentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentService {
    Page<ContentDto> getAll(Pageable pageable);

    Page<ContentDto> getNewContents(Pageable pageable);

    ContentDto getById(String id);
    ContentDto getBySlug(String slug,String ownerId);
    ContentDto save(ContentDto contentDto);
    ContentDto update(String id, ContentDto contentDto);
    void delete(String id);
}
