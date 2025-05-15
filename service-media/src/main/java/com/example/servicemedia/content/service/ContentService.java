package com.example.servicemedia.content.service;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.feign.like.LikeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ContentService {
    Page<ContentDto> getAll(Pageable pageable);
    Page<ContentDto> filter(Pageable pageable,String categoryId,String name);

    ContentDto getByTop(LikeType likeType);

    Long getCount();

    Content findById(String id);

    ContentDto getById(String id);
    ContentDto getBySlug(String slug);

    ContentDto save(ContentDto contentDto);
    void saveContentsBulk(List<ContentDto> contentDtoList);
    ContentDto update(String id, ContentDto contentDto);
    void delete(String id);
}
