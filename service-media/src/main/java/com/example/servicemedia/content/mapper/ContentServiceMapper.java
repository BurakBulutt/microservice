package com.example.servicemedia.content.mapper;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.model.Content;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentServiceMapper {

    public static ContentDto toDto(Content content) {
        return ContentDto.builder()
                .id(content.getId())
                .name(content.getName())
                .description(content.getDescription())
                .photoUrl(content.getPhotoUrl())
                .type(content.getType())
                .subject(content.getSubject())
                .startDate(content.getStartDate())
                .slug(content.getSlug())
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
        return content;
    }

}
