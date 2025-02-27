package com.example.servicemedia.content.dto;

import com.example.servicemedia.content.enums.ContentType;
import com.example.servicemedia.media.dto.MediaDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ContentDto {
    @JsonIgnore
    private String id;
    private String name;
    private String description;
    private String photoUrl;
    private ContentType type;
    private String subject;
    private Date startDate;
    private String slug;
    private List<MediaDto> mediaList;
}
