package com.example.servicemedia.domain.content.service;

import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.feign.like.LikeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


public interface ContentService {
    Page<ContentDto> getAll(Pageable pageable);

    ContentDto getByTop(LikeType likeType);

    Long getCount();

    Content findById(String id);

    Page<ContentDto> filter(Pageable pageable, String category, String query, LocalDate firstDate, LocalDate lastDate, ContentType type);

    List<ContentDto> search(String query);

    ContentDto getById(String id);
    ContentDto getBySlug(String slug);

    ContentDto save(ContentDto contentDto);
    void saveContentsBulk(List<ContentDto> contentDtoList);
    ContentDto update(String id, ContentDto contentDto);
    void delete(String id);
}
