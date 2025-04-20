package com.example.servicemedia.content.service;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ContentService {
    Page<ContentDto> getAll(Pageable pageable);
    Page<ContentDto> filter(Pageable pageable,String categoryId,String name);

    Content findById(String id);

    ContentDto getById(String id);
    ContentDto getBySlug(String slug);

    void save(ContentDto contentDto);
    ContentDto update(String id, ContentDto contentDto);
    void delete(String id);
}
