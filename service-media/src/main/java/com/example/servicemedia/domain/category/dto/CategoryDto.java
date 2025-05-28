package com.example.servicemedia.domain.category.dto;

import com.example.servicemedia.domain.content.dto.ContentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CategoryDto {
    private String id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String name;
    private String description;
    private String slug;
    private List<ContentDto> contents;
}
