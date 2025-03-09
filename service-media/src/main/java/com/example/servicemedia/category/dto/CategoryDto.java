package com.example.servicemedia.category.dto;

import com.example.servicemedia.content.dto.ContentDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryDto {
    private String id;
    private String name;
    private String description;
    private String slug;
    private List<ContentDto> contents;
}
