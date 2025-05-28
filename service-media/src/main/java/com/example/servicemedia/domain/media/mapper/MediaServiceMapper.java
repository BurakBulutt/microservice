package com.example.servicemedia.domain.media.mapper;

import com.example.servicemedia.domain.content.model.Content;
import com.example.servicemedia.domain.fansub.dto.FansubDto;
import com.example.servicemedia.domain.fansub.mapper.FansubServiceMapper;
import com.example.servicemedia.domain.media.dto.MediaDto;
import com.example.servicemedia.domain.media.dto.MediaSourceDto;
import com.example.servicemedia.domain.media.model.Media;
import com.example.servicemedia.domain.media.model.MediaSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MediaServiceMapper {

    public static MediaDto toDto(Media media){
        return MediaDto.builder()
                .id(media.getId())
                .created(media.getCreated())
                .modified(media.getModified())
                .name(media.getName())
                .description(media.getDescription())
                .count(media.getCount())
                .publishDate(media.getPublishDate())
                .slug(media.getSlug())
                .build();
    }

    public static MediaSourceDto toMediaSourceDto(MediaSource mediaSource) {
        FansubDto fansub = FansubServiceMapper.toDto(mediaSource.getFansub());

        return MediaSourceDto.builder()
                .id(mediaSource.getId())
                .url(mediaSource.getUrl())
                .type(mediaSource.getType())
                .mediaId(mediaSource.getMedia().getId())
                .fansub(fansub)
                .build();
    }

    public static Media toEntity(Media media, Content content,MediaDto dto) {
        media.setName(dto.getName());
        media.setDescription(dto.getDescription());
        media.setCount(dto.getCount());
        media.setPublishDate(dto.getPublishDate());
        media.setSlug(dto.getSlug());
        media.setContent(content);

        return media;
    }
}
