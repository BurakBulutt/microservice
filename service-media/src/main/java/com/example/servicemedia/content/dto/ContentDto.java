package com.example.servicemedia.content.dto;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.content.enums.ContentType;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.media.dto.MediaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ContentDto {
    private String id;
    private String name;
    private String description;
    private String photoUrl;
    private ContentType type;
    private String subject;
    private Date startDate;
    private String slug;
    private List<MediaDto> medias;
    private List<CategoryDto> categories;
    private LikeCountResponse likeCount;
    private Integer episodeTime;
}
