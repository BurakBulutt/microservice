package com.example.servicemedia.content.mapper;

import com.example.servicemedia.content.api.ContentRequest;
import com.example.servicemedia.content.api.ContentResponse;
import com.example.servicemedia.content.dto.ContentDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

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
                .build();
    }

    public static Page<ContentResponse> toPageResponse(Page<ContentDto> page) {
        return page.map(ContentApiMapper::toResponse);
    }
}
