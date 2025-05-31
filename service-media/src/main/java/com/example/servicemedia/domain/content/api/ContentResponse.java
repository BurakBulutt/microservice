package com.example.servicemedia.domain.content.api;

import com.example.servicemedia.domain.category.dto.CategoryDto;
import com.example.servicemedia.domain.content.enums.ContentType;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.domain.media.dto.MediaDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ContentResponse {
    private String id;
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
