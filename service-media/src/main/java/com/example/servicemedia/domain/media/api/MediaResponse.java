package com.example.servicemedia.domain.media.api;

import com.example.servicemedia.domain.content.dto.ContentDto;
import com.example.servicemedia.feign.like.LikeCountResponse;
import com.example.servicemedia.domain.media.dto.MediaSourceDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class MediaResponse {
    private String id;
    private String name;
    private String description;
    private ContentDto content;
    private Integer count;
    private LocalDate publishDate;
    private String slug;
    private List<MediaSourceDto> mediaSourceList;
    private LikeCountResponse likeCount;
}
