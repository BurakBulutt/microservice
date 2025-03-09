package com.example.servicemedia.media.api;

import com.example.servicemedia.feign.LikeCountResponse;
import com.example.servicemedia.media.dto.MediaSourceDto;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class MediaResponse {
    private String id;
    private String name;
    private String description;
    private String contentId;
    private Integer count;
    private Date publishDate;
    private String slug;
    private List<MediaSourceDto> mediaSourceList;
    private LikeCountResponse likeCount;
}
