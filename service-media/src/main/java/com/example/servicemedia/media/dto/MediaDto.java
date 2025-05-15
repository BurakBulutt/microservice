package com.example.servicemedia.media.dto;

import com.example.servicemedia.content.dto.ContentDto;
import com.example.servicemedia.feign.like.LikeCountResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MediaDto {
    private String id;
    private String name;
    private String description;
    private ContentDto content;
    private Integer count;
    private List<MediaSourceDto> mediaSourceList;
    private LocalDate publishDate;
    private String slug;
    private LikeCountResponse likeCount;
}
