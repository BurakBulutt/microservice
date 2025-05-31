package com.example.servicemedia.domain.content.mapper;

import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.domain.content.model.Content;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentServiceMapper {

    public static ContentDto toDto(Content content) {
        return ContentDto.builder()
                .id(content.getId())
                .created(content.getCreated())
                .modified(content.getModified())
                .name(content.getName())
                .description(content.getDescription())
                .photoUrl(content.getPhotoUrl())
                .type(content.getType())
                .subject(content.getSubject())
                .startDate(content.getStartDate())
                .slug(content.getSlug())
                .episodeTime(content.getEpisodeTime())
                .build();
    }

    public static Content toEntity(Content content,ContentDto contentDto) {
        content.setName(contentDto.getName());
        content.setDescription(contentDto.getDescription());
        content.setPhotoUrl(contentDto.getPhotoUrl());
        content.setType(contentDto.getType());
        content.setSubject(contentDto.getSubject());
        content.setStartDate(contentDto.getStartDate());
        content.setSlug(contentDto.getSlug());
        content.setEpisodeTime(contentDto.getEpisodeTime());

        return content;
    }
}
