package com.example.servicemedia.domain.media.mapper;

import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.media.api.MediaRequest;
import com.example.servicemedia.domain.media.api.MediaResponse;
import com.example.servicemedia.domain.media.api.MediaSourceResponse;
import com.example.servicemedia.domain.media.api.UpdateMediaSourceRequest;
import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.dto.MediaSourceDto;
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

    public static MediaSourceResponse toMediaSourceResponse(MediaSourceDto dto) {
        return MediaSourceResponse.builder()
                .id(dto.getId())
                .type(dto.getType())
                .url(dto.getUrl())
                .media(dto.getMedia())
                .fansub(dto.getFansub())
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

    public static List<MediaSourceDto> toMediaSourceDtoList(UpdateMediaSourceRequest request) {
        return request.mediaSourceRequestList().stream().map(mediaSourceRequest -> MediaSourceDto.builder()
                .url(mediaSourceRequest.url())
                .type(mediaSourceRequest.type())
                .media(MediaDto.builder().id(mediaSourceRequest.mediaId()).build())
                .fansub(FansubDto.builder()
                        .name(mediaSourceRequest.fansub().name())
                        .url(mediaSourceRequest.fansub().url())
                        .build())
                .build()).toList();
    }

    public static Page<MediaResponse> toPageResponse(Page<MediaDto> dtoPage) {
        return dtoPage.map(MediaApiMapper::toResponse);
    }

    public static List<MediaSourceResponse> toMediaSourceDataResponse(List<MediaSourceDto> mediaSources) {
        return mediaSources.stream().map(MediaApiMapper::toMediaSourceResponse).toList();
    }
}
