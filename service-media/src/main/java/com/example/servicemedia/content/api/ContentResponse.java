package com.example.servicemedia.content.api;

import com.example.servicemedia.category.dto.CategoryDto;
import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.content.enums.ContentType;
import com.example.servicemedia.feign.LikeCountResponse;
import com.example.servicemedia.media.dto.MediaDto;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class ContentResponse {
    private String id;
    private String name;
    private String description;
    private String photoUrl;
    private ContentType type;
    private String subject;
    private Date startDate;
    private String slug;
    private List<MediaDto> mediaList;
    private LikeCountResponse likeCount;
    private List<CategoryDto> categories;
    private Integer episodeTime;
}
