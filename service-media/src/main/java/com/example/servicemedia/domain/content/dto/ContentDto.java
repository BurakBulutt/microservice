package com.example.servicemedia.domain.content.dto;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.domain.media.dto.MediaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ContentDto {
    private String id;
    private LocalDateTime created;
    private LocalDateTime modified;
    private String name;
    private String description;
    private String photoUrl;
    private ContentType type;
    private String subject;
    private LocalDate startDate;
    private String slug;
    private List<MediaDto> medias;
    private List<CategoryDto> categories;
    private Integer episodeTime;
    private LikeCountResponse likeCount;
}
