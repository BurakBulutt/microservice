package com.example.servicemedia.media.mapper;

import com.example.servicemedia.content.model.Content;
import com.example.servicemedia.media.dto.MediaDto;
import com.example.servicemedia.media.dto.MediaSourceDto;
import com.example.servicemedia.media.model.Media;
import com.example.servicemedia.media.model.MediaSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MediaServiceMapper {

    public static MediaDto toDto(Media media){
        return MediaDto.builder()
                .id(media.getId())
                .name(media.getName())
                .description(media.getDescription())
                .count(media.getCount())
                .publishDate(media.getPublishDate())
                .slug(media.getSlug())
                .numberOfViews(media.getNumberOfViews())
                .build();
    }

    public static MediaSourceDto toMediaSourceDto(MediaSource mediaSource) {
        return MediaSourceDto.builder()
                .id(mediaSource.getId())
                .url(mediaSource.getUrl())
                .type(mediaSource.getType())
                .mediaId(mediaSource.getMedia().getId())
                .fanSub(mediaSource.getFanSub())
                .build();
    }

    public static Media toEntity(Media media, Content content,MediaDto dto) {
        media.setName(dto.getName());
        media.setDescription(dto.getDescription());
        media.setCount(dto.getCount());
        media.setPublishDate(dto.getPublishDate());
        media.setSlug(dto.getSlug());
        media.setContent(content);
        media.setNumberOfViews(dto.getNumberOfViews());

        return media;
    }
}
