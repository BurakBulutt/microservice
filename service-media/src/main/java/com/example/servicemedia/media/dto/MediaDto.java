package com.example.servicemedia.media.dto;

import com.example.servicemedia.feign.LikeCountResponse;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class MediaDto {
    private String id;
    private String name;
    private String description;
    private String contentId;
    private Integer count;
    private List<MediaSourceDto> mediaSourceList;
    private Date publishDate;
    private String slug;
    private LikeCountResponse likeCount;
}
