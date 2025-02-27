package com.example.servicemedia.media.mapper;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.media.api.MediaRequest;
import com.example.servicemedia.media.api.MediaResponse;
import com.example.servicemedia.media.dto.MediaDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MediaApiMapper {

    public static MediaResponse toResponse(MediaDto dto) {
        return MediaResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .count(dto.getCount())
                .slug(dto.getSlug())
                .publishDate(dto.getPublishDate())
                .content(dto.getContent())
                .mediaSourceList(dto.getMediaSourceList())
                .build();
    }

    public static MediaDto toDto(MediaRequest request) {
        return MediaDto.builder()
                .description(request.description())
                .content(ContentDto.builder().id(request.contentId()).build())
                .slug(request.slug())
                .mediaSourceList(request.mediaSourceList())
                .publishDate(request.publishDate())
                .count(request.count())
                .build();
    }

    public static Page<MediaResponse> toPageResponse(Page<MediaDto> dtoPage) {
        return dtoPage.map(MediaApiMapper::toResponse);
    }
}
