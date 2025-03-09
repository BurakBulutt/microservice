package com.example.servicemedia.content.mapper;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.content.api.ContentRequest;
import com.example.servicemedia.content.api.ContentResponse;
import com.example.servicemedia.content.dto.ContentDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentApiMapper {

    public static ContentResponse toResponse(ContentDto dto) {
        return ContentResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .photoUrl(dto.getPhotoUrl())
                .type(dto.getType())
                .subject(dto.getSubject())
                .startDate(dto.getStartDate())
                .slug(dto.getSlug())
                .mediaList(dto.getMediaList())
                .likeCount(dto.getLikeCount())
                .categories(dto.getCategories())
                .episodeTime(dto.getEpisodeTime())
                .build();
    }

    public static ContentDto toDto(ContentRequest request) {
        return ContentDto.builder()
                .name(request.name())
                .description(request.description())
                .photoUrl(request.photoUrl())
                .type(request.type())
                .subject(request.subject())
                .startDate(request.startDate())
                .slug(request.slug())
                .categories(request.categoryIds() != null ? request.categoryIds().stream().map(id -> CategoryDto.builder().id(id).build()).toList() : Collections.emptyList())
                .episodeTime(request.episodeTime())
                .build();
    }

    public static Page<ContentResponse> toPageResponse(Page<ContentDto> page) {
        return page.map(ContentApiMapper::toResponse);
    }
}
