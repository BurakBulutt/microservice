package com.example.servicemedia.domain.category.api;

import com.example.servicemedia.domain.content.dto.ContentDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryResponse {
    private String id;
    private String name;
    private String description;
    private String slug;
    private List<ContentDto> contents;
}
