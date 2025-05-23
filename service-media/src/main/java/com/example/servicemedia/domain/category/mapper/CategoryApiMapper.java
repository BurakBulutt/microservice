package com.example.servicemedia.domain.category.mapper;

import com.example.servicemedia.domain.category.api.CategoryRequest;
import com.example.servicemedia.domain.category.api.CategoryResponse;
import com.example.servicemedia.domain.category.dto.CategoryDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryApiMapper {

    public static CategoryResponse toResponse(CategoryDto dto){
        return CategoryResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .slug(dto.getSlug())
                .contents(dto.getContents())
                .build();
    }

    public static CategoryDto toDto(CategoryRequest request){
        return CategoryDto.builder()
                .name(request.name().trim())
                .description(request.description())
                .slug(request.slug())
                .build();
    }

    public static Page<CategoryResponse> toPageResponse(Page<CategoryDto> dtoPage){
        return dtoPage.map(CategoryApiMapper::toResponse);
    }
}
