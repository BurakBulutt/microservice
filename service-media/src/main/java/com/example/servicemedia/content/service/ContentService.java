package com.example.servicemedia.content.service;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.model.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContentService {
    Page<ContentDto> getAll(Pageable pageable,String name);
    Page<ContentDto> filter(String category,String sortBy,Pageable pageable);

    List<ContentDto> searchFilter(String query);

    Page<ContentDto> getNewContents();

    ContentDto getById(String id);

    Content findById(String id);

    ContentDto getBySlug(String slug);

    void save(ContentDto contentDto);
    void update(String id, ContentDto contentDto);
    void delete(String id);
}
