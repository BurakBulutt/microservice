package com.example.servicemedia.domain.category.mapper;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.category.model.Category;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryServiceMapper {

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .created(category.getCreated())
                .modified(category.getModified())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .build();
    }

    public static Category toEntity(Category category,CategoryDto dto) {
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setSlug(dto.getSlug());

        return category;
    }
 }
