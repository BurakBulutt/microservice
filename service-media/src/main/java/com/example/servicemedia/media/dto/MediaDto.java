package com.example.servicemedia.media.dto;

import com.example.servicemedia.content.dto.ContentDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class MediaDto {
    @JsonIgnore
    private String id;
    private Date created;
    private Date modified;
    private String name;
    private String description;
    private ContentDto content;
    private Integer count;
    private List<MediaSourceDto> mediaSourceList;
    private Date publishDate;
    private String slug;
}
