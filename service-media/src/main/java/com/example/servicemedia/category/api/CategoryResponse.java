package com.example.servicemedia.category.api;

import com.example.servicemedia.content.dto.ContentDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class CategoryResponse {
    private String id;
    private String name;
    private String description;
    private String slug;
    private List<ContentDto> contents;
}
