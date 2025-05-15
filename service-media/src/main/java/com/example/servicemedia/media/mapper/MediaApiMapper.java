package com.example.servicemedia.media.mapper;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.media.api.MediaNameResponse;
import com.example.servicemedia.media.api.MediaRequest;
import com.example.servicemedia.media.api.MediaResponse;
import com.example.servicemedia.media.api.MediaSourceResponse;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

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
                .likeCount(dto.getLikeCount())
                .mediaSourceList(dto.getMediaSourceList())
                .build();
    }

    public static MediaNameResponse toNameResponse(MediaDto dto) {
        return MediaNameResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    public static MediaSourceResponse toMediaSourceResponse(MediaSourceDto dto) {
        return MediaSourceResponse.builder()
                .id(dto.getId())
                .type(dto.getType())
                .url(dto.getUrl())
                .mediaId(dto.getMediaId())
                .fanSub(dto.getFanSub())
                .build();
    }

    public static MediaDto toDto(MediaRequest request) {
        return MediaDto.builder()
                .description(request.description())
                .content(ContentDto.builder().id(request.contentId()).build())
                .publishDate(request.publishDate())
                .count(request.count())
                .build();
    }

    public static Page<MediaResponse> toPageResponse(Page<MediaDto> dtoPage) {
        return dtoPage.map(MediaApiMapper::toResponse);
    }

    public static List<MediaSourceResponse> toMediaSourceDataResponse(List<MediaSourceDto> mediaSources) {
        return mediaSources.stream().map(MediaApiMapper::toMediaSourceResponse).toList();
    }
}
